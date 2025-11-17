package com.lineinc.erp.api.server.domain.aggregation.constructionoutsourcing.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.aggregation.outsourcingcompany.service.OutsourcingCompanyDeductionAggregationService;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstruction;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstructionGroup;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportOutsourcingConstructionGroupRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractConstructionRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ConstructionOutsourcingAggregationDetailRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ConstructionOutsourcingAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.DeductionAmountAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingCompaniesResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.DeductionAmountAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionGroupResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractListResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

/**
 * 외주(공사) 외주업체 집계 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConstructionOutsourcingCompanyAggregationService {

    private final DailyReportOutsourcingConstructionGroupRepository dailyReportOutsourcingConstructionGroupRepository;
    private final OutsourcingCompanyContractConstructionRepository outsourcingCompanyContractConstructionRepository;
    private final OutsourcingCompanyDeductionAggregationService outsourcingCompanyDeductionAggregationService;

    /**
     * 외주(공사) 외주업체 목록 조회
     * 현장, 공정으로 필터링하여 외주업체와 외주업체계약을 중복 제거 후 반환
     */
    public List<ConstructionOutsourcingCompaniesResponse> getConstructionOutsourcingCompanies(
            final Long siteId,
            final Long siteProcessId) {
        return dailyReportOutsourcingConstructionGroupRepository
                .findBySiteAndSiteProcess(siteId, siteProcessId)
                .stream()
                .map(group -> ConstructionOutsourcingCompaniesResponse.from(
                        group.getOutsourcingCompanyContractConstructionGroup().getOutsourcingCompanyContract()))
                .distinct()
                .toList();
    }

    /**
     * 외주(공사) 집계 상세 조회
     * 해당 계약의 모든 항목별로 전회/금회 수량을 집계하여 반환
     */
    public ConstructionOutsourcingAggregationDetailResponse getConstructionOutsourcingAggregationDetail(
            final ConstructionOutsourcingAggregationDetailRequest request) {
        final YearMonth ym = YearMonth.parse(request.yearMonth());
        final LocalDate startMonth = ym.atDay(1);
        final LocalDate nextMonthStart = ym.plusMonths(1).atDay(1);
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startMonth);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthStart);

        // 해당 계약의 모든 공사항목 조회
        final List<OutsourcingCompanyContractConstruction> allConstructions = outsourcingCompanyContractConstructionRepository
                .findAllByOutsourcingCompanyContractId(request.outsourcingCompanyContractId());

        // 조회월 다음달 1일 미만까지의 모든 출역일보 데이터 조회
        final var groups = dailyReportOutsourcingConstructionGroupRepository
                .findBySiteAndSiteProcessAndContractIdAndReportDateLessThan(
                        request.siteId(),
                        request.siteProcessId(),
                        request.outsourcingCompanyContractId(),
                        endExclusive);

        // 공사항목 ID별로 수량 및 금액 집계
        final Map<Long, BillingAccumulator> billingMap = new HashMap<>();
        for (final var group : groups) {
            for (final DailyReportOutsourcingConstruction construction : group.getConstructions()) {
                if (construction.getOutsourcingCompanyContractConstruction() == null) {
                    continue;
                }
                final OutsourcingCompanyContractConstruction contractConstruction = construction
                        .getOutsourcingCompanyContractConstruction();
                final Long constructionId = contractConstruction.getId();
                final OffsetDateTime reportDate = group.getDailyReport().getReportDate();
                final Integer quantity = construction.getQuantity();
                final Long unitPrice = contractConstruction.getOutsourcingContractUnitPrice();

                if (quantity == null || reportDate == null) {
                    continue;
                }

                final BillingAccumulator accumulator = billingMap.computeIfAbsent(constructionId,
                        k -> new BillingAccumulator());
                final long amount = (unitPrice != null ? unitPrice : 0L) * quantity;

                if (reportDate.isBefore(startInclusive)) {
                    accumulator.previousQuantity += quantity;
                    accumulator.previousAmount += amount;
                } else {
                    accumulator.currentQuantity += quantity;
                    accumulator.currentAmount += amount;
                }
            }
        }

        // 그룹별로 공사항목 그룹핑
        final Map<Long, List<OutsourcingCompanyContractConstruction>> constructionsByGroup = new HashMap<>();
        for (final OutsourcingCompanyContractConstruction construction : allConstructions) {
            final Long groupId = construction.getConstructionGroup() != null
                    ? construction.getConstructionGroup().getId()
                    : 0L; // 그룹이 없는 경우 0으로 처리
            constructionsByGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(construction);
        }

        // 응답 생성 (그룹별로)
        final List<ConstructionOutsourcingAggregationDetailResponse.ConstructionGroupItem> groupItems = new ArrayList<>();
        for (final Map.Entry<Long, List<OutsourcingCompanyContractConstruction>> entry : constructionsByGroup
                .entrySet()) {
            final List<OutsourcingCompanyContractConstruction> constructions = entry.getValue();

            // 그룹 정보 가져오기 (첫 번째 공사항목의 그룹 사용)
            final var firstConstruction = constructions.get(0);
            final var constructionGroup = firstConstruction.getConstructionGroup();

            // 공사항목 목록 생성
            final List<ConstructionOutsourcingAggregationDetailResponse.ConstructionItem> constructionItems = new ArrayList<>();
            for (final OutsourcingCompanyContractConstruction construction : constructions) {
                final BillingAccumulator accumulator = billingMap.getOrDefault(construction.getId(),
                        new BillingAccumulator());
                constructionItems.add(new ConstructionOutsourcingAggregationDetailResponse.ConstructionItem(
                        construction.getId(), // constructionId
                        construction.getItem(),
                        construction.getSpecification(),
                        construction.getUnit(),
                        construction.getUnitPrice(),
                        construction.getContractQuantity(),
                        construction.getContractPrice(),
                        construction.getOutsourcingContractQuantity(),
                        construction.getOutsourcingContractUnitPrice(),
                        construction.getOutsourcingContractPrice(),
                        new ConstructionOutsourcingAggregationDetailResponse.BillingDetail(
                                accumulator.previousQuantity > 0 ? accumulator.previousQuantity : null,
                                accumulator.previousAmount > 0 ? accumulator.previousAmount : null),
                        new ConstructionOutsourcingAggregationDetailResponse.BillingDetail(
                                accumulator.currentQuantity > 0 ? accumulator.currentQuantity : null,
                                accumulator.currentAmount > 0 ? accumulator.currentAmount : null)));
            }

            groupItems.add(new ConstructionOutsourcingAggregationDetailResponse.ConstructionGroupItem(
                    constructionGroup != null
                            ? ContractConstructionGroupResponse.ContractConstructionGroupSimpleResponseForDailyReport
                                    .from(constructionGroup)
                            : null,
                    constructionItems));
        }

        return new ConstructionOutsourcingAggregationDetailResponse(groupItems);
    }

    /**
     * 외주(공사) 집계 조회
     * 외주업체 계약별로 전회/금회 청구내역과 공제금액을 집계하여 반환
     */
    public ConstructionOutsourcingAggregationResponse getConstructionOutsourcingAggregation(
            final ConstructionOutsourcingAggregationRequest request) {
        final YearMonth ym = YearMonth.parse(request.yearMonth());
        final LocalDate startMonth = ym.atDay(1);
        final LocalDate nextMonthStart = ym.plusMonths(1).atDay(1);
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startMonth);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthStart);

        // 조회월 다음달 1일 미만까지의 모든 출역일보 데이터 조회
        final List<DailyReportOutsourcingConstructionGroup> groups = dailyReportOutsourcingConstructionGroupRepository
                .findBySiteAndSiteProcessAndReportDateLessThan(
                        request.siteId(),
                        request.siteProcessId(),
                        endExclusive);

        // 외주업체 계약별로 청구내역 집계
        final Map<Long, ContractBillingAccumulator> contractBillingMap = new HashMap<>();
        for (final DailyReportOutsourcingConstructionGroup group : groups) {
            // 계약 정보 가져오기
            if (group.getOutsourcingCompanyContractConstructionGroup() == null
                    || group.getOutsourcingCompanyContractConstructionGroup().getOutsourcingCompanyContract() == null) {
                continue;
            }

            final OutsourcingCompanyContract contract = group.getOutsourcingCompanyContractConstructionGroup()
                    .getOutsourcingCompanyContract();
            final Long contractId = contract.getId();
            final ContractBillingAccumulator accumulator = contractBillingMap.computeIfAbsent(contractId,
                    k -> new ContractBillingAccumulator(contract));

            final OffsetDateTime reportDate = group.getDailyReport().getReportDate();
            if (reportDate == null) {
                continue;
            }

            // 공사항목별로 금액 계산 (단가 × 수량)
            for (final DailyReportOutsourcingConstruction construction : group.getConstructions()) {
                if (construction.getOutsourcingCompanyContractConstruction() == null) {
                    continue;
                }

                final OutsourcingCompanyContractConstruction contractConstruction = construction
                        .getOutsourcingCompanyContractConstruction();
                final Integer quantity = construction.getQuantity();
                final Long unitPrice = contractConstruction.getOutsourcingContractUnitPrice();

                if (quantity == null) {
                    continue;
                }

                // 금액 = 단가 × 수량
                final long amount = (unitPrice != null ? unitPrice : 0L) * quantity;

                if (reportDate.isBefore(startInclusive)) {
                    accumulator.previousAmount += amount;
                } else {
                    accumulator.currentAmount += amount;
                }
            }
        }

        // 외주업체 계약별로 공제금액 집계
        for (final Map.Entry<Long, ContractBillingAccumulator> entry : contractBillingMap.entrySet()) {
            final Long contractId = entry.getKey();
            final ContractBillingAccumulator accumulator = entry.getValue();

            // 해당 계약의 공제금액 계산
            final DeductionAmountAggregationResponse deductionResponse = outsourcingCompanyDeductionAggregationService
                    .getDeductionAmountAggregation(
                            new DeductionAmountAggregationRequest(
                                    request.siteId(),
                                    request.siteProcessId(),
                                    request.yearMonth(),
                                    contractId));

            // 식대, 간식대, 유류대, 자재비 공제금액 합산
            long previousDeduction = 0L;
            long currentDeduction = 0L;

            if (deductionResponse.mealFee() != null) {
                if (deductionResponse.mealFee().previousBilling() != null
                        && deductionResponse.mealFee().previousBilling().totalAmount() != null) {
                    previousDeduction += deductionResponse.mealFee().previousBilling().totalAmount();
                }
                if (deductionResponse.mealFee().currentBilling() != null
                        && deductionResponse.mealFee().currentBilling().totalAmount() != null) {
                    currentDeduction += deductionResponse.mealFee().currentBilling().totalAmount();
                }
            }
            if (deductionResponse.snackFee() != null) {
                if (deductionResponse.snackFee().previousBilling() != null
                        && deductionResponse.snackFee().previousBilling().totalAmount() != null) {
                    previousDeduction += deductionResponse.snackFee().previousBilling().totalAmount();
                }
                if (deductionResponse.snackFee().currentBilling() != null
                        && deductionResponse.snackFee().currentBilling().totalAmount() != null) {
                    currentDeduction += deductionResponse.snackFee().currentBilling().totalAmount();
                }
            }
            if (deductionResponse.fuelFee() != null) {
                if (deductionResponse.fuelFee().previousBilling() != null
                        && deductionResponse.fuelFee().previousBilling().totalAmount() != null) {
                    previousDeduction += deductionResponse.fuelFee().previousBilling().totalAmount();
                }
                if (deductionResponse.fuelFee().currentBilling() != null
                        && deductionResponse.fuelFee().currentBilling().totalAmount() != null) {
                    currentDeduction += deductionResponse.fuelFee().currentBilling().totalAmount();
                }
            }
            if (deductionResponse.materialCost() != null) {
                if (deductionResponse.materialCost().previousBilling() != null
                        && deductionResponse.materialCost().previousBilling().totalAmount() != null) {
                    previousDeduction += deductionResponse.materialCost().previousBilling().totalAmount();
                }
                if (deductionResponse.materialCost().currentBilling() != null
                        && deductionResponse.materialCost().currentBilling().totalAmount() != null) {
                    currentDeduction += deductionResponse.materialCost().currentBilling().totalAmount();
                }
            }

            accumulator.previousDeduction = previousDeduction;
            accumulator.currentDeduction = currentDeduction;

            // 공급가액 = 금액 - 공제금액
            accumulator.previousSupplyPrice = accumulator.previousAmount - previousDeduction;
            accumulator.currentSupplyPrice = accumulator.currentAmount - currentDeduction;

            // 부가세 = 공급가액 × 10%
            accumulator.previousVat = accumulator.previousSupplyPrice / 10;
            accumulator.currentVat = accumulator.currentSupplyPrice / 10;

            // 총액 = 공급가액 + 부가세
            accumulator.previousTotal = accumulator.previousSupplyPrice + accumulator.previousVat;
            accumulator.currentTotal = accumulator.currentSupplyPrice + accumulator.currentVat;
        }

        // 응답 생성
        final List<ConstructionOutsourcingAggregationResponse.ConstructionOutsourcingAggregationItem> items = contractBillingMap
                .values()
                .stream()
                .filter(acc -> acc.previousAmount > 0 || acc.currentAmount > 0)
                .map(acc -> {
                    final ContractListResponse.ContractSimpleResponse contractSimple = ContractListResponse.ContractSimpleResponse
                            .from(acc.contract);

                    return new ConstructionOutsourcingAggregationResponse.ConstructionOutsourcingAggregationItem(
                            contractSimple,
                            // 전회 청구내역: 값이 없어도 0으로 표시
                            new ConstructionOutsourcingAggregationResponse.BillingDetail(
                                    acc.previousSupplyPrice,
                                    acc.previousVat,
                                    acc.previousDeduction,
                                    acc.previousTotal),
                            // 금회 청구내역: 값이 없어도 0으로 표시
                            new ConstructionOutsourcingAggregationResponse.BillingDetail(
                                    acc.currentSupplyPrice,
                                    acc.currentVat,
                                    acc.currentDeduction,
                                    acc.currentTotal));
                })
                .toList();

        return new ConstructionOutsourcingAggregationResponse(items);
    }

    /**
     * 청구내역 누적을 위한 보조 클래스
     */
    private static final class BillingAccumulator {
        int previousQuantity = 0;
        long previousAmount = 0;
        int currentQuantity = 0;
        long currentAmount = 0;
    }

    /**
     * 외주업체 계약별 청구내역 누적을 위한 보조 클래스
     */
    private static final class ContractBillingAccumulator {
        final OutsourcingCompanyContract contract;
        long previousAmount = 0; // 금액 (단가 × 수량)
        long previousSupplyPrice = 0; // 공급가액 (금액 - 공제금액)
        long previousVat = 0; // 부가세 (공급가액 × 10%)
        long previousDeduction = 0; // 공제금액
        long previousTotal = 0; // 총액 (공급가액 + 부가세)
        long currentAmount = 0; // 금액 (단가 × 수량)
        long currentSupplyPrice = 0; // 공급가액 (금액 - 공제금액)
        long currentVat = 0; // 부가세 (공급가액 × 10%)
        long currentDeduction = 0; // 공제금액
        long currentTotal = 0; // 총액 (공급가액 + 부가세)

        ContractBillingAccumulator(final OutsourcingCompanyContract contract) {
            this.contract = contract;
        }
    }
}

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

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstruction;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportOutsourcingConstructionGroupRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractConstructionRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ConstructionOutsourcingAggregationDetailRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingCompaniesResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionGroupResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionResponse;
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

        // 응답 생성
        final List<ConstructionOutsourcingAggregationDetailResponse.ConstructionOutsourcingAggregationDetailItem> items = new ArrayList<>();
        for (final OutsourcingCompanyContractConstruction construction : allConstructions) {
            final BillingAccumulator accumulator = billingMap.getOrDefault(construction.getId(),
                    new BillingAccumulator());
            items.add(new ConstructionOutsourcingAggregationDetailResponse.ConstructionOutsourcingAggregationDetailItem(
                    construction.getConstructionGroup() != null
                            ? ContractConstructionGroupResponse.ContractConstructionGroupSimpleResponseForDailyReport
                                    .from(construction.getConstructionGroup())
                            : null,
                    ContractConstructionResponse.ContractConstructionSimpleResponse.from(construction),
                    new ConstructionOutsourcingAggregationDetailResponse.BillingDetail(
                            accumulator.previousQuantity > 0 ? accumulator.previousQuantity : null,
                            accumulator.previousAmount > 0 ? accumulator.previousAmount : null),
                    new ConstructionOutsourcingAggregationDetailResponse.BillingDetail(
                            accumulator.currentQuantity > 0 ? accumulator.currentQuantity : null,
                            accumulator.currentAmount > 0 ? accumulator.currentAmount : null)));
        }

        return new ConstructionOutsourcingAggregationDetailResponse(items);
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
}

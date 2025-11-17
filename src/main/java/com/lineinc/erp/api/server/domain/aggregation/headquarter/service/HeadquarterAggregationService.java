package com.lineinc.erp.api.server.domain.aggregation.headquarter.service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.aggregation.constructionoutsourcing.service.ConstructionOutsourcingCompanyAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.equipmentcost.service.EquipmentCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.laborcost.service.LaborCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.managementcost.service.ManagementCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.materialcost.service.MaterialCostAggregationService;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository.OutsourcingCompanyContractRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.sitemanagementcost.repository.SiteManagementCostRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ConstructionOutsourcingAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingAggregationResponse.ConstructionOutsourcingAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse.EquipmentCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.HeadquarterAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.HeadquarterAggregationResponse.CostSummary;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.LaborCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.LaborCostAggregationResponse.LaborCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse.ManagementCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse;

import lombok.RequiredArgsConstructor;

/**
 * 본사 집계 서비스
 * <p>
 * 단기 대응: 기존 집계 응답을 재활용하여 전회/금회 합계를 구한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HeadquarterAggregationService {

    private final MaterialCostAggregationService materialCostAggregationService;
    private final LaborCostAggregationService laborCostAggregationService;
    private final EquipmentCostAggregationService equipmentCostAggregationService;
    private final ManagementCostAggregationService managementCostAggregationService;
    private final ConstructionOutsourcingCompanyAggregationService constructionOutsourcingCompanyAggregationService;
    private final SiteRepository siteRepository;
    private final SiteProcessRepository siteProcessRepository;
    private final OutsourcingCompanyContractRepository outsourcingCompanyContractRepository;
    private final SiteManagementCostRepository siteManagementCostRepository;

    /**
     * 본사 집계 조회
     *
     * @param siteId        현장 ID
     * @param siteProcessId 공정 ID
     * @param yearMonth     조회월 (YYYY-MM)
     * @return 본사 집계 응답
     */
    public HeadquarterAggregationResponse getHeadquarterAggregation(
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth) {

        // 모든 집계 서비스를 병렬로 실행
        final CompletableFuture<MaterialCostAggregationResponse> materialFuture = CompletableFuture
                .supplyAsync(() -> materialCostAggregationService.getMaterialCostAggregation(siteId, siteProcessId,
                        yearMonth));

        // 노무비 집계 (직영 + 용역) 병렬 처리
        final CompletableFuture<LaborCostAggregationResponse> directContractFuture = CompletableFuture
                .supplyAsync(() -> laborCostAggregationService.getLaborCostAggregation(
                        siteId,
                        siteProcessId,
                        yearMonth,
                        LaborType.DIRECT_CONTRACT));
        final CompletableFuture<LaborCostAggregationResponse> outsourcingFuture = CompletableFuture
                .supplyAsync(() -> laborCostAggregationService.getLaborCostAggregation(
                        siteId,
                        siteProcessId,
                        yearMonth,
                        LaborType.OUTSOURCING));
        final CompletableFuture<List<LaborCostAggregationResponse>> laborFuture = directContractFuture
                .thenCombine(outsourcingFuture, (direct, outsourcing) -> List.of(direct, outsourcing));

        final CompletableFuture<EquipmentCostAggregationResponse> equipmentFuture = CompletableFuture
                .supplyAsync(() -> equipmentCostAggregationService.getEquipmentCostAggregation(siteId, siteProcessId,
                        yearMonth));

        final CompletableFuture<ConstructionOutsourcingAggregationResponse> constructionOutsourcingFuture = CompletableFuture
                .supplyAsync(() -> constructionOutsourcingCompanyAggregationService
                        .getConstructionOutsourcingAggregation(
                                new ConstructionOutsourcingAggregationRequest(siteId, siteProcessId, yearMonth)));

        final CompletableFuture<ManagementCostAggregationResponse> managementFuture = CompletableFuture
                .supplyAsync(() -> managementCostAggregationService.getManagementCostAggregation(
                        new ManagementCostAggregationRequest(siteId, siteProcessId, yearMonth)));

        final CompletableFuture<Long> totalConstructionAmountFuture = CompletableFuture
                .supplyAsync(() -> calculateTotalConstructionAmount(siteId));

        // 현장/본사 관리비 조회 (병렬 처리)
        final CompletableFuture<SiteManagementCostInfo> siteManagementCostFuture = CompletableFuture
                .supplyAsync(() -> getSiteManagementCostInfo(siteId, siteProcessId, yearMonth));

        // 모든 결과를 기다림
        final MaterialCostAggregationResponse materialResponse = materialFuture.join();
        final List<LaborCostAggregationResponse> laborResponses = laborFuture.join();
        final EquipmentCostAggregationResponse equipmentResponse = equipmentFuture.join();
        final ConstructionOutsourcingAggregationResponse constructionOutsourcingResponse = constructionOutsourcingFuture
                .join();
        final ManagementCostAggregationResponse managementResponse = managementFuture.join();
        final long totalConstructionAmount = totalConstructionAmountFuture.join();
        final SiteManagementCostInfo siteManagementCostInfo = siteManagementCostFuture.join();

        // 요약 생성
        final CostSummary materialSummary = buildSummary("재료비",
                current -> materialBillingDetails(materialResponse, current));
        final CostSummary laborSummary = buildSummary("노무비",
                current -> laborBillingDetails(laborResponses, current));
        final CostSummary equipmentSummary = buildSummary("장비비",
                current -> equipmentBillingDetails(equipmentResponse, current));
        final CostSummary constructionOutsourcingSummary = buildSummary("외주비",
                current -> constructionOutsourcingBillingDetails(constructionOutsourcingResponse, current));
        final CostSummary managementSummary = buildSummary("관리비",
                current -> managementBillingDetails(managementResponse, current));

        // 현장관리비: 공급가, 부가세, 공제금액을 각각 합산
        final CostSummary siteManagementCostSummary = buildSiteManagementCostSummary(
                "현장관리비",
                siteManagementCostInfo.previousSiteManagementSupplyPrice(),
                siteManagementCostInfo.previousSiteManagementVat(),
                siteManagementCostInfo.previousSiteManagementDeduction(),
                siteManagementCostInfo.previousSiteManagementTotal(),
                siteManagementCostInfo.currentSiteManagementSupplyPrice(),
                siteManagementCostInfo.currentSiteManagementVat(),
                siteManagementCostInfo.currentSiteManagementDeduction(),
                siteManagementCostInfo.currentSiteManagementTotal());
        // 본사관리비: 공급가와 계만 있음 (부가세, 공제금액은 0)
        final CostSummary headquartersManagementCostSummary = buildHeadquartersManagementCostSummary(
                "본사관리비",
                siteManagementCostInfo.previousHeadquartersManagementCost(),
                siteManagementCostInfo.currentHeadquartersManagementCost());

        return new HeadquarterAggregationResponse(
                totalConstructionAmount,
                List.of(materialSummary, laborSummary, equipmentSummary, constructionOutsourcingSummary,
                        managementSummary, siteManagementCostSummary, headquartersManagementCostSummary));
    }

    /**
     * 비용 항목 요약 생성
     */
    private CostSummary buildSummary(
            final String costName,
            final BillingDetailStreamSupplier detailSupplier) {
        return new CostSummary(
                costName,
                sumBillingDetails(detailSupplier.get(false)),
                sumBillingDetails(detailSupplier.get(true)));
    }

    /**
     * 현장관리비 요약 생성
     * 공급가, 부가세, 공제금액을 각각 합산
     */
    private CostSummary buildSiteManagementCostSummary(
            final String costName,
            final Long previousSupplyPrice,
            final Long previousVat,
            final Long previousDeduction,
            final Long previousTotal,
            final Long currentSupplyPrice,
            final Long currentVat,
            final Long currentDeduction,
            final Long currentTotal) {
        final HeadquarterAggregationResponse.BillingSummary previousSummary = new HeadquarterAggregationResponse.BillingSummary(
                previousSupplyPrice != null ? previousSupplyPrice : 0L,
                previousVat != null ? previousVat : 0L,
                previousDeduction != null ? previousDeduction : 0L,
                previousTotal != null ? previousTotal : 0L);
        final HeadquarterAggregationResponse.BillingSummary currentSummary = new HeadquarterAggregationResponse.BillingSummary(
                currentSupplyPrice != null ? currentSupplyPrice : 0L,
                currentVat != null ? currentVat : 0L,
                currentDeduction != null ? currentDeduction : 0L,
                currentTotal != null ? currentTotal : 0L);

        return new CostSummary(
                costName,
                previousSummary,
                currentSummary);
    }

    /**
     * 본사관리비 요약 생성
     * 공급가와 계만 있고, 부가세와 공제금액은 0
     */
    private CostSummary buildHeadquartersManagementCostSummary(
            final String costName,
            final Long previousManagementCost,
            final Long currentManagementCost) {
        final Long previousSupplyPrice = previousManagementCost != null ? previousManagementCost : 0L;
        final Long previousVat = 0L;
        final Long previousDeductionAmount = 0L;
        final Long previousTotal = previousSupplyPrice; // 공급가 = 계 (부가세, 공제금액이 0이므로)

        final Long currentSupplyPrice = currentManagementCost != null ? currentManagementCost : 0L;
        final Long currentVat = 0L;
        final Long currentDeductionAmount = 0L;
        final Long currentTotal = currentSupplyPrice; // 공급가 = 계 (부가세, 공제금액이 0이므로)

        final HeadquarterAggregationResponse.BillingSummary previousSummary = new HeadquarterAggregationResponse.BillingSummary(
                previousSupplyPrice, previousVat, previousDeductionAmount, previousTotal);
        final HeadquarterAggregationResponse.BillingSummary currentSummary = new HeadquarterAggregationResponse.BillingSummary(
                currentSupplyPrice, currentVat, currentDeductionAmount, currentTotal);

        return new CostSummary(
                costName,
                previousSummary,
                currentSummary);
    }

    private MaterialManagementItemResponse.BillingDetail pickBilling(
            final MaterialManagementItemResponse.BillingDetail previous,
            final MaterialManagementItemResponse.BillingDetail current,
            final boolean useCurrent) {
        return useCurrent ? current : previous;
    }

    private MaterialManagementItemResponse.BillingDetail pickBilling(
            final LaborCostAggregationItem item,
            final boolean useCurrent) {
        return pickBilling(item.previousBilling(), item.currentBilling(), useCurrent);
    }

    private MaterialManagementItemResponse.BillingDetail pickBilling(
            final EquipmentCostAggregationItem item,
            final boolean useCurrent) {
        return pickBilling(item.previousBilling(), item.currentBilling(), useCurrent);
    }

    /**
     * 총 공사금액 = 현장 기본 계약금액 + 외주업체 계약금액 합계
     */
    private long calculateTotalConstructionAmount(final Long siteId) {
        final long siteContractAmount = toLong(siteRepository.findContractAmountBySiteId(siteId));
        final long outsourcingContractAmount = toLong(
                outsourcingCompanyContractRepository.sumContractAmountBySiteId(siteId));
        return siteContractAmount + outsourcingContractAmount;
    }

    private long toLong(final Long value) {
        return value != null ? value : 0L;
    }

    private long[] accumulate(
            final Stream<MaterialManagementItemResponse.BillingDetail> details) {

        final long[] totals = new long[4]; // 0: supply, 1: vat, 2: deduction, 3: total

        details
                .filter(Objects::nonNull)
                .forEach(detail -> {
                    totals[0] += toLong(detail.supplyPrice());
                    totals[1] += toLong(detail.vat());
                    totals[2] += toLong(detail.deductionAmount());
                    totals[3] += toLong(detail.total());
                });

        return totals;
    }

    private HeadquarterAggregationResponse.BillingSummary sumBillingDetails(
            final Stream<MaterialManagementItemResponse.BillingDetail> details) {
        final long[] totals = accumulate(details);
        return new HeadquarterAggregationResponse.BillingSummary(totals[0], totals[1], totals[2], totals[3]);
    }

    private Stream<MaterialManagementItemResponse.BillingDetail> materialBillingDetails(
            final MaterialCostAggregationResponse response,
            final boolean current) {
        final Stream<MaterialManagementItemResponse.BillingDetail> materialDetails = response.materialManagements()
                .stream()
                .map(item -> pickBilling(item.previousBilling(), item.currentBilling(), current));

        final Stream<MaterialManagementItemResponse.BillingDetail> steelDetails = response.steelManagements()
                .stream()
                .map(item -> pickBilling(item.previousBilling(), item.currentBilling(), current));

        final Stream<MaterialManagementItemResponse.BillingDetail> fuelDetails = response.fuelAggregations()
                .stream()
                .map(item -> pickBilling(item.previousBilling(), item.currentBilling(), current));

        return Stream.concat(Stream.concat(materialDetails, steelDetails), fuelDetails);
    }

    private Stream<MaterialManagementItemResponse.BillingDetail> laborBillingDetails(
            final List<LaborCostAggregationResponse> responses,
            final boolean current) {
        return responses.stream()
                .flatMap(response -> response.items().stream())
                .map(item -> pickBilling(item, current));
    }

    private Stream<MaterialManagementItemResponse.BillingDetail> equipmentBillingDetails(
            final EquipmentCostAggregationResponse response,
            final boolean current) {
        return response.items().stream()
                .map(item -> pickBilling(item, current));
    }

    private Stream<MaterialManagementItemResponse.BillingDetail> constructionOutsourcingBillingDetails(
            final ConstructionOutsourcingAggregationResponse response,
            final boolean current) {
        if (response == null || response.items() == null) {
            return Stream.empty();
        }

        return response.items().stream()
                .map(item -> pickBilling(item, current));
    }

    private Stream<MaterialManagementItemResponse.BillingDetail> managementBillingDetails(
            final ManagementCostAggregationResponse response,
            final boolean current) {
        if (response == null || response.items() == null) {
            return Stream.empty();
        }

        return response.items().stream()
                .map(item -> pickBilling(item, current));
    }

    private MaterialManagementItemResponse.BillingDetail pickBilling(
            final ConstructionOutsourcingAggregationItem item,
            final boolean useCurrent) {
        if (item == null) {
            return null;
        }
        final var billing = useCurrent ? item.currentBilling() : item.previousBilling();
        if (billing == null) {
            return null;
        }

        return new MaterialManagementItemResponse.BillingDetail(
                Long.valueOf(billing.supplyPrice()),
                Long.valueOf(billing.vat()),
                Long.valueOf(billing.deduction()),
                Long.valueOf(billing.total()));
    }

    private MaterialManagementItemResponse.BillingDetail pickBilling(
            final ManagementCostAggregationItem item,
            final boolean useCurrent) {
        if (item == null) {
            return null;
        }
        final var billing = useCurrent ? item.currentBilling() : item.previousBilling();
        if (billing == null) {
            return null;
        }

        return new MaterialManagementItemResponse.BillingDetail(
                Long.valueOf(billing.supplyPrice()),
                Long.valueOf(billing.vat()),
                Long.valueOf(billing.deduction()),
                Long.valueOf(billing.total()));
    }

    /**
     * 현장/본사 관리비 정보 조회
     * 전회: 조회월 이전까지의 합계, 금회: 조회월의 값
     */
    private SiteManagementCostInfo getSiteManagementCostInfo(
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth) {
        final Site site = siteRepository.findById(siteId).orElse(null);
        final SiteProcess siteProcess = siteProcessRepository.findById(siteProcessId).orElse(null);

        if (site == null || siteProcess == null) {
            return new SiteManagementCostInfo(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }

        // 전회: 조회월 이전까지의 모든 현장관리비 합계
        final List<com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost> previousCosts = siteManagementCostRepository
                .findByYearMonthLessThanAndSiteAndSiteProcess(yearMonth, site, siteProcess);

        long previousSiteManagementSupplyPrice = 0L;
        long previousSiteManagementVat = 0L;
        long previousSiteManagementDeduction = 0L;
        long previousSiteManagementTotal = 0L;
        long previousHeadquartersManagementCost = 0L;
        for (final var cost : previousCosts) {
            final Long supplyPrice = cost.calculateSiteManagementSupplyPrice();
            if (supplyPrice != null) {
                previousSiteManagementSupplyPrice += supplyPrice;
            }
            final Long vat = cost.calculateSiteManagementVat();
            if (vat != null) {
                previousSiteManagementVat += vat;
            }
            final Long deduction = cost.calculateSiteManagementDeduction();
            if (deduction != null) {
                previousSiteManagementDeduction += deduction;
            }
            final Long siteTotal = cost.calculateSiteManagementTotal();
            if (siteTotal != null) {
                previousSiteManagementTotal += siteTotal;
            }
            final Long headquartersCost = cost.getHeadquartersManagementCost();
            if (headquartersCost != null) {
                previousHeadquartersManagementCost += headquartersCost;
            }
        }

        // 금회: 조회월의 현장관리비
        final var currentSiteManagementCost = siteManagementCostRepository
                .findByYearMonthAndSiteAndSiteProcess(yearMonth, site, siteProcess)
                .orElse(null);

        final Long currentSiteManagementSupplyPrice = currentSiteManagementCost != null
                ? currentSiteManagementCost.calculateSiteManagementSupplyPrice()
                : 0L;
        final Long currentSiteManagementVat = currentSiteManagementCost != null
                ? currentSiteManagementCost.calculateSiteManagementVat()
                : 0L;
        final Long currentSiteManagementDeduction = currentSiteManagementCost != null
                ? currentSiteManagementCost.calculateSiteManagementDeduction()
                : 0L;
        final Long currentSiteManagementTotal = currentSiteManagementCost != null
                ? currentSiteManagementCost.calculateSiteManagementTotal()
                : 0L;
        final Long currentHeadquartersManagementCost = currentSiteManagementCost != null
                ? currentSiteManagementCost.getHeadquartersManagementCost()
                : 0L;

        return new SiteManagementCostInfo(
                previousSiteManagementSupplyPrice,
                previousSiteManagementVat,
                previousSiteManagementDeduction,
                previousSiteManagementTotal,
                currentSiteManagementSupplyPrice != null ? currentSiteManagementSupplyPrice : 0L,
                currentSiteManagementVat != null ? currentSiteManagementVat : 0L,
                currentSiteManagementDeduction != null ? currentSiteManagementDeduction : 0L,
                currentSiteManagementTotal != null ? currentSiteManagementTotal : 0L,
                previousHeadquartersManagementCost,
                currentHeadquartersManagementCost != null ? currentHeadquartersManagementCost : 0L);
    }

    /**
     * 현장/본사 관리비 정보를 담는 내부 클래스
     */
    private record SiteManagementCostInfo(
            Long previousSiteManagementSupplyPrice,
            Long previousSiteManagementVat,
            Long previousSiteManagementDeduction,
            Long previousSiteManagementTotal,
            Long currentSiteManagementSupplyPrice,
            Long currentSiteManagementVat,
            Long currentSiteManagementDeduction,
            Long currentSiteManagementTotal,
            Long previousHeadquartersManagementCost,
            Long currentHeadquartersManagementCost) {
    }

    @FunctionalInterface
    private interface BillingDetailStreamSupplier {
        Stream<MaterialManagementItemResponse.BillingDetail> get(boolean current);
    }
}

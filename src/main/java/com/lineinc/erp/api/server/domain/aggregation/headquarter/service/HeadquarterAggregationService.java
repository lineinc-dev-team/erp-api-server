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
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
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
    private final OutsourcingCompanyContractRepository outsourcingCompanyContractRepository;

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

        // 모든 결과를 기다림
        final MaterialCostAggregationResponse materialResponse = materialFuture.join();
        final List<LaborCostAggregationResponse> laborResponses = laborFuture.join();
        final EquipmentCostAggregationResponse equipmentResponse = equipmentFuture.join();
        final ConstructionOutsourcingAggregationResponse constructionOutsourcingResponse = constructionOutsourcingFuture
                .join();
        final ManagementCostAggregationResponse managementResponse = managementFuture.join();
        final long totalConstructionAmount = totalConstructionAmountFuture.join();

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

        return new HeadquarterAggregationResponse(
                totalConstructionAmount,
                List.of(materialSummary, laborSummary, equipmentSummary, constructionOutsourcingSummary,
                        managementSummary));
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

    @FunctionalInterface
    private interface BillingDetailStreamSupplier {
        Stream<MaterialManagementItemResponse.BillingDetail> get(boolean current);
    }
}

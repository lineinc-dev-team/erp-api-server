package com.lineinc.erp.api.server.domain.aggregation.headquarter.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.aggregation.equipmentcost.service.EquipmentCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.laborcost.service.LaborCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.materialcost.service.MaterialCostAggregationService;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse.EquipmentCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.HeadquarterAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.HeadquarterAggregationResponse.CostSummary;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.LaborCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.LaborCostAggregationResponse.LaborCostAggregationItem;
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

        // 1) 재료비 집계 응답 확보
        final MaterialCostAggregationResponse materialResponse = materialCostAggregationService
                .getMaterialCostAggregation(siteId, siteProcessId, yearMonth);

        final CostSummary materialSummary = buildSummary("재료비",
                current -> materialBillingDetails(materialResponse, current));

        // 2) 노무비 집계 응답 확보 (직영 + 용역만 포함)
        final List<LaborCostAggregationResponse> laborResponses = Stream.of(
                LaborType.DIRECT_CONTRACT,
                LaborType.OUTSOURCING)
                .map(laborType -> laborCostAggregationService.getLaborCostAggregation(
                        siteId,
                        siteProcessId,
                        yearMonth,
                        laborType))
                .toList();

        final CostSummary laborSummary = buildSummary("노무비",
                current -> laborBillingDetails(laborResponses, current));

        // 3) 장비비 집계 응답 확보
        final EquipmentCostAggregationResponse equipmentResponse = equipmentCostAggregationService
                .getEquipmentCostAggregation(siteId, siteProcessId, yearMonth);

        final CostSummary equipmentSummary = buildSummary("장비비",
                current -> equipmentBillingDetails(equipmentResponse, current));

        return new HeadquarterAggregationResponse(List.of(materialSummary, laborSummary, equipmentSummary));
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

    @FunctionalInterface
    private interface BillingDetailStreamSupplier {
        Stream<MaterialManagementItemResponse.BillingDetail> get(boolean current);
    }
}

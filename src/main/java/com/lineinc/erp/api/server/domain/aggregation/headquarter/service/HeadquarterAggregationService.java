package com.lineinc.erp.api.server.domain.aggregation.headquarter.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.aggregation.materialcost.service.MaterialCostAggregationService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.HeadquarterAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.HeadquarterAggregationResponse.BillingSummary;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.HeadquarterAggregationResponse.CostSummary;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.FuelAggregationItemResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.SteelManagementItemResponse;

import lombok.RequiredArgsConstructor;

/**
 * 본사 집계 서비스
 * <p>
 * 단기 대응: 기존 재료비 집계 응답을 재활용하여 전회/금회 합계를 구한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HeadquarterAggregationService {

    private final MaterialCostAggregationService materialCostAggregationService;
    private final SiteProcessService siteProcessService;

    /**
     * 본사 집계 조회
     *
     * @param siteId        현장 ID
     * @param siteProcessId 공정 ID
     * @param yearMonth     조회월 (YYYY-MM)
     * @return 재료비 기준 본사 집계 응답
     */
    public HeadquarterAggregationResponse getHeadquarterAggregation(
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth) {

        // 기존 재료비 집계 API 활용
        final MaterialCostAggregationResponse materialResponse = materialCostAggregationService
                .getMaterialCostAggregation(siteId, siteProcessId, yearMonth);

        // 재료비 전회/금회 합계 계산
        final BillingSummary previousSummary = sumMaterialBilling(materialResponse, false);
        final BillingSummary currentSummary = sumMaterialBilling(materialResponse, true);

        final CostSummary materialSummary = new CostSummary(
                "재료비",
                previousSummary,
                currentSummary);

        return new HeadquarterAggregationResponse(List.of(materialSummary));
    }

    /**
     * 재료비 응답의 전회/금회 청구내역 합계를 계산한다.
     */
    private BillingSummary sumMaterialBilling(
            final MaterialCostAggregationResponse materialResponse,
            final boolean current) {

        final long[] totals = new long[4]; // 0: supply, 1: vat, 2: deduction, 3: total

        billingDetails(materialResponse, current)
                .filter(Objects::nonNull)
                .forEach(detail -> {
                    totals[0] += toLong(detail.supplyPrice());
                    totals[1] += toLong(detail.vat());
                    totals[2] += toLong(detail.deductionAmount());
                    totals[3] += toLong(detail.total());
                });

        return new BillingSummary(totals[0], totals[1], totals[2], totals[3]);
    }

    private Stream<MaterialManagementItemResponse.BillingDetail> billingDetails(
            final MaterialCostAggregationResponse response,
            final boolean current) {
        final Stream<MaterialManagementItemResponse.BillingDetail> materialDetails = response.materialManagements()
                .stream()
                .map(item -> selectBilling(item, current));

        final Stream<MaterialManagementItemResponse.BillingDetail> steelDetails = response.steelManagements()
                .stream()
                .map(item -> selectBilling(item, current));

        final Stream<MaterialManagementItemResponse.BillingDetail> fuelDetails = response.fuelAggregations()
                .stream()
                .map(item -> selectBilling(item, current));

        return Stream.concat(Stream.concat(materialDetails, steelDetails), fuelDetails);
    }

    private MaterialManagementItemResponse.BillingDetail selectBilling(
            final MaterialManagementItemResponse item,
            final boolean current) {
        return current ? item.currentBilling() : item.previousBilling();
    }

    private MaterialManagementItemResponse.BillingDetail selectBilling(
            final SteelManagementItemResponse item,
            final boolean current) {
        return current ? item.currentBilling() : item.previousBilling();
    }

    private MaterialManagementItemResponse.BillingDetail selectBilling(
            final FuelAggregationItemResponse item,
            final boolean current) {
        return current ? item.currentBilling() : item.previousBilling();
    }

    private long toLong(final Long value) {
        return value != null ? value : 0L;
    }
}

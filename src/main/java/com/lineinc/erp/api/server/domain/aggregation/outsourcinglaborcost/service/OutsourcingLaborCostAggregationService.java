package com.lineinc.erp.api.server.domain.aggregation.outsourcinglaborcost.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.laborpayroll.repository.LaborPayrollRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.OutsourcingLaborCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.OutsourcingLaborCostAggregationResponse.OutsourcingLaborCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 용역 노무비 집계 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutsourcingLaborCostAggregationService {

    private final LaborPayrollRepository laborPayrollRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;

    /**
     * 용역 노무비 집계 조회 (외주업체별)
     *
     * @param siteId        현장 ID
     * @param siteProcessId 공정 ID
     * @param yearMonth     조회월 (YYYY-MM)
     * @return 용역 노무비 집계 응답
     */
    public OutsourcingLaborCostAggregationResponse getOutsourcingLaborCostAggregation(
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth) {

        final Site site = siteService.getSiteByIdOrThrow(siteId);
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(siteProcessId);

        // 용역 인력의 노무비 명세서 조회
        final List<LaborPayroll> allPayrolls = findOutsourcingPayrollsUpToMonth(site, siteProcess, yearMonth);

        // 외주업체별로 그룹핑하여 집계
        final List<OutsourcingLaborCostAggregationItem> items = aggregateByOutsourcingCompany(allPayrolls,
                yearMonth);

        return new OutsourcingLaborCostAggregationResponse(items);
    }

    /**
     * 용역 인력의 노무비 명세서 조회 (DB 레벨에서 필터링)
     */
    private List<LaborPayroll> findOutsourcingPayrollsUpToMonth(
            final Site site,
            final SiteProcess siteProcess,
            final String yearMonth) {
        // 용역(OUTSOURCING) 타입으로 필터링
        return laborPayrollRepository.findBySiteAndSiteProcessAndYearMonthLessThanEqualAndLaborType(
                site.getId(),
                siteProcess.getId(),
                yearMonth,
                LaborType.OUTSOURCING);
    }

    /**
     * 외주업체별로 노무비 명세서 집계
     */
    private List<OutsourcingLaborCostAggregationItem> aggregateByOutsourcingCompany(
            final List<LaborPayroll> allPayrolls,
            final String currentYearMonth) {

        // 외주업체 ID별로 그룹핑
        final Map<Long, List<LaborPayroll>> groupedByCompany = allPayrolls.stream()
                .filter(payroll -> payroll.getLabor() != null)
                .filter(payroll -> payroll.getLabor().getOutsourcingCompany() != null)
                .collect(Collectors.groupingBy(payroll -> payroll.getLabor().getOutsourcingCompany().getId()));

        return groupedByCompany.entrySet().stream()
                .map(entry -> createOutsourcingLaborCostAggregationItem(entry.getValue(), currentYearMonth))
                .filter(this::hasNonZeroBilling)
                .toList();
    }

    /**
     * 외주업체별 집계 항목 생성
     */
    private OutsourcingLaborCostAggregationItem createOutsourcingLaborCostAggregationItem(
            final List<LaborPayroll> payrolls,
            final String currentYearMonth) {

        final LaborPayroll firstPayroll = payrolls.get(0);

        // 외주업체 정보
        final CompanyResponse.CompanySimpleResponse outsourcingCompany = CompanyResponse.CompanySimpleResponse
                .from(firstPayroll.getLabor().getOutsourcingCompany());

        // 전회까지 청구내역 (현재 월 이전)
        final BillingDetail previousBilling = aggregatePreviousBilling(payrolls, currentYearMonth);

        // 금회 청구내역 (현재 월)
        final BillingDetail currentBilling = aggregateCurrentBilling(payrolls, currentYearMonth);

        return new OutsourcingLaborCostAggregationItem(outsourcingCompany, previousBilling, currentBilling);
    }

    /**
     * 전회까지 청구내역 집계
     */
    private BillingDetail aggregatePreviousBilling(
            final List<LaborPayroll> payrolls,
            final String currentYearMonth) {

        final List<LaborPayroll> previousPayrolls = payrolls.stream()
                .filter(payroll -> payroll.getYearMonth() != null)
                .filter(payroll -> payroll.getYearMonth().compareTo(currentYearMonth) < 0)
                .toList();

        return calculateBillingDetail(previousPayrolls);
    }

    /**
     * 금회 청구내역 집계
     */
    private BillingDetail aggregateCurrentBilling(
            final List<LaborPayroll> payrolls,
            final String currentYearMonth) {

        final List<LaborPayroll> currentPayrolls = payrolls.stream()
                .filter(payroll -> currentYearMonth.equals(payroll.getYearMonth()))
                .toList();

        return calculateBillingDetail(currentPayrolls);
    }

    /**
     * 청구내역 계산
     * 공급가 = 노무비 총액
     * 부가세 = 0
     * 공제금액 = 총 공제액
     * 계 = 차감지급액
     */
    private BillingDetail calculateBillingDetail(final List<LaborPayroll> payrolls) {
        long supplyPrice = 0L;
        final long vat = 0L; // 노무비는 부가세 없음
        long deductionAmount = 0L;
        long total = 0L;

        for (final LaborPayroll payroll : payrolls) {
            // 공급가 = 노무비 총액
            supplyPrice += getValueOrZero(payroll.getTotalLaborCost());

            // 공제금액 = 총 공제액
            deductionAmount += getValueOrZero(payroll.getTotalDeductions());

            // 계 = 차감지급액
            total += getValueOrZero(payroll.getNetPayment());
        }

        return new BillingDetail(supplyPrice, vat, deductionAmount, total);
    }

    /**
     * BigDecimal 값을 Long으로 변환, null이면 0 반환
     */
    private long getValueOrZero(final BigDecimal value) {
        return value != null ? value.longValue() : 0L;
    }

    /**
     * 전회까지 또는 금회 청구내역 중 하나라도 0이 아닌지 확인
     */
    private boolean hasNonZeroBilling(final OutsourcingLaborCostAggregationItem item) {
        return item.previousBilling().total() != 0 || item.currentBilling().total() != 0;
    }
}

package com.lineinc.erp.api.server.domain.aggregation.equipmentcost.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipment;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipmentSubEquipment;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportOutsourcingEquipmentRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse.EquipmentCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 장비비 집계 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentCostAggregationService {

    private final DailyReportOutsourcingEquipmentRepository equipmentRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private static final BigDecimal VAT_RATE = BigDecimal.valueOf(0.1);
    private static final BigDecimal ONE_PLUS_VAT = BigDecimal.ONE.add(VAT_RATE);

    /**
     * 장비비 집계 조회 (외주업체별)
     *
     * @param siteId        현장 ID
     * @param siteProcessId 공정 ID
     * @param yearMonth     조회월 (YYYY-MM)
     * @return 장비비 집계 응답
     */
    public EquipmentCostAggregationResponse getEquipmentCostAggregation(
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth) {

        final Site site = siteService.getSiteByIdOrThrow(siteId);
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(siteProcessId);

        // 해당 월까지의 외주업체 장비 데이터 조회
        final List<DailyReportOutsourcingEquipment> allEquipments = findEquipmentsUpToMonth(site, siteProcess,
                yearMonth);

        // 장비(규격)별로 그룹핑하여 집계
        final List<EquipmentCostAggregationItem> items = aggregateByEquipment(allEquipments, yearMonth);

        return new EquipmentCostAggregationResponse(items);
    }

    /**
     * 해당 월까지의 외주업체 장비 데이터 조회
     */
    private List<DailyReportOutsourcingEquipment> findEquipmentsUpToMonth(
            final Site site,
            final SiteProcess siteProcess,
            final String yearMonth) {

        final YearMonth ym = YearMonth.parse(yearMonth);
        // 조회월의 마지막 날
        final LocalDate lastDay = ym.atEndOfMonth();
        final OffsetDateTime endDateTime = DateTimeFormatUtils.toUtcEndOfDay(lastDay);

        return equipmentRepository.findBySiteAndSiteProcessAndReportDateLessThanEqual(
                site.getId(),
                siteProcess.getId(),
                endDateTime);
    }

    /**
     * 장비(규격)별로 장비비 집계
     */
    private List<EquipmentCostAggregationItem> aggregateByEquipment(
            final List<DailyReportOutsourcingEquipment> allEquipments,
            final String currentYearMonth) {

        // 장비 ID로 그룹핑 (장비, 규격별)
        final Map<Long, List<DailyReportOutsourcingEquipment>> groupedByEquipment = allEquipments.stream()
                .filter(equipment -> equipment.getOutsourcingCompany() != null)
                .filter(equipment -> equipment.getOutsourcingCompanyContractEquipment() != null)
                .collect(
                        Collectors.groupingBy(equipment -> equipment.getOutsourcingCompanyContractEquipment().getId()));

        return groupedByEquipment.entrySet().stream()
                .map(entry -> createEquipmentCostAggregationItem(entry.getValue(), currentYearMonth))
                .filter(this::hasNonZeroBilling)
                .toList();
    }

    /**
     * 장비, 규격별 집계 항목 생성
     */
    private EquipmentCostAggregationItem createEquipmentCostAggregationItem(
            final List<DailyReportOutsourcingEquipment> equipments,
            final String currentYearMonth) {

        final DailyReportOutsourcingEquipment firstEquipment = equipments.get(0);

        // 외주업체 정보
        final CompanyResponse.CompanySimpleResponse outsourcingCompany = CompanyResponse.CompanySimpleResponse
                .from(firstEquipment.getOutsourcingCompany());

        // 장비 정보
        final String specification = firstEquipment.getOutsourcingCompanyContractEquipment().getSpecification();

        // 전회까지 청구내역 (현재 월 이전)
        final BillingDetail previousBilling = aggregatePreviousBilling(equipments, currentYearMonth);

        // 금회 청구내역 (현재 월)
        final BillingDetail currentBilling = aggregateCurrentBilling(equipments, currentYearMonth);

        return new EquipmentCostAggregationItem(outsourcingCompany, specification, previousBilling,
                currentBilling);
    }

    /**
     * 전회까지 청구내역 집계
     */
    private BillingDetail aggregatePreviousBilling(
            final List<DailyReportOutsourcingEquipment> equipments,
            final String currentYearMonth) {

        final YearMonth currentYm = YearMonth.parse(currentYearMonth);

        final List<DailyReportOutsourcingEquipment> previousEquipments = equipments.stream()
                .filter(equipment -> equipment.getDailyReport() != null)
                .filter(equipment -> equipment.getDailyReport().getReportDate() != null)
                .filter(equipment -> {
                    final LocalDate reportDate = DateTimeFormatUtils
                            .toKoreaLocalDate(equipment.getDailyReport().getReportDate());
                    final YearMonth reportYm = YearMonth.from(reportDate);
                    return reportYm.isBefore(currentYm);
                })
                .toList();

        return calculateBillingDetail(previousEquipments);
    }

    /**
     * 금회 청구내역 집계
     */
    private BillingDetail aggregateCurrentBilling(
            final List<DailyReportOutsourcingEquipment> equipments,
            final String currentYearMonth) {

        final YearMonth currentYm = YearMonth.parse(currentYearMonth);

        final List<DailyReportOutsourcingEquipment> currentEquipments = equipments.stream()
                .filter(equipment -> equipment.getDailyReport() != null)
                .filter(equipment -> equipment.getDailyReport().getReportDate() != null)
                .filter(equipment -> {
                    final LocalDate reportDate = DateTimeFormatUtils
                            .toKoreaLocalDate(equipment.getDailyReport().getReportDate());
                    final YearMonth reportYm = YearMonth.from(reportDate);
                    return reportYm.equals(currentYm);
                })
                .toList();

        return calculateBillingDetail(currentEquipments);
    }

    /**
     * 청구내역 계산
     * 공급가 = 장비 단가×시간 + 서브장비들의 (단가×시간) 합계
     * 부가세 = 공급가 × 10%
     * 공제금액 = 0
     * 계 = 공급가 + 부가세
     */
    private BillingDetail calculateBillingDetail(final List<DailyReportOutsourcingEquipment> equipments) {
        long totalGross = 0L;

        for (final DailyReportOutsourcingEquipment equipment : equipments) {
            // 장비 총액(부가세 포함)을 단가 × 시간으로 추정
            final long equipmentTotalCost = calculateEquipmentCost(equipment);
            totalGross += equipmentTotalCost;

            // 서브장비 비용 합산
            for (final DailyReportOutsourcingEquipmentSubEquipment subEquipment : equipment.getSubEquipments()) {
                final long subEquipmentTotalCost = calculateSubEquipmentCost(subEquipment);
                totalGross += subEquipmentTotalCost;
            }
        }

        // 공급가와 부가세를 역산
        final long supplyPrice = calculateSupplyPriceFromTotal(totalGross);
        final long vat = totalGross - supplyPrice;

        // 공제금액 = 0 (장비비는 공제 없음)
        final long deductionAmount = 0L;

        // 계 = 총액 (부가세 포함)
        final long total = totalGross;

        return new BillingDetail(supplyPrice, vat, deductionAmount, total);
    }

    /**
     * 장비 총액(부가세 포함) 계산 (단가 × 시간)
     */
    private long calculateEquipmentCost(final DailyReportOutsourcingEquipment equipment) {
        final long unitPrice = equipment.getUnitPrice() != null ? equipment.getUnitPrice() : 0L;
        final double workHours = equipment.getWorkHours() != null ? equipment.getWorkHours() : 0.0;
        return Math.round(unitPrice * workHours);
    }

    /**
     * 서브장비 총액(부가세 포함) 계산 (단가 × 시간)
     */
    private long calculateSubEquipmentCost(final DailyReportOutsourcingEquipmentSubEquipment subEquipment) {
        final long unitPrice = subEquipment.getUnitPrice() != null ? subEquipment.getUnitPrice() : 0L;
        final double workHours = subEquipment.getWorkHours() != null ? subEquipment.getWorkHours() : 0.0;
        return Math.round(unitPrice * workHours);
    }

    /**
     * 총액(부가세 포함)을 기준으로 공급가를 역산
     */
    private long calculateSupplyPriceFromTotal(final long totalGross) {
        if (totalGross <= 0) {
            return 0L;
        }

        return BigDecimal.valueOf(totalGross)
                .divide(ONE_PLUS_VAT, 0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    /**
     * 전회까지 또는 금회 청구내역 중 하나라도 0이 아닌지 확인
     */
    private boolean hasNonZeroBilling(final EquipmentCostAggregationItem item) {
        return item.previousBilling().total() != 0 || item.currentBilling().total() != 0;
    }
}

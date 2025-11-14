package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.aggregation.equipmentcost.service.EquipmentCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.laborcost.service.LaborCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.managementcost.service.ManagementCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.materialcost.service.MaterialCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.outsourcinglaborcost.service.OutsourcingLaborCostAggregationService;
import com.lineinc.erp.api.server.domain.dashboard.entity.SiteMonthlyCostSummary;
import com.lineinc.erp.api.server.domain.dashboard.repository.SiteMonthlyCostSummaryRepository;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.LaborCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.OutsourcingLaborCostAggregationResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 대시보드 현장 월별 비용 집계 배치 서비스
 * 대시보드 현장 목록 조회로 반환되는 현장들에 대해 각 월마다 재료비, 노무비, 관리비, 장비비, 외주비를 저장합니다.
 * 기준: 착공일(시작일)이 포함된 월부터 배치 실행 시점의 월까지 계산합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardSiteMonthlyCostBatchService implements BatchService {

    private final SiteRepository siteRepository;
    private final MaterialCostAggregationService materialCostAggregationService;
    private final LaborCostAggregationService laborCostAggregationService;
    private final ManagementCostAggregationService managementCostAggregationService;
    private final EquipmentCostAggregationService equipmentCostAggregationService;
    private final OutsourcingLaborCostAggregationService outsourcingLaborCostAggregationService;
    private final SiteMonthlyCostSummaryRepository siteMonthlyCostSummaryRepository;

    @Override
    public String getBatchName() {
        return "대시보드 현장 월별 비용 집계 배치";
    }

    @Override
    @Transactional
    public void execute() throws Exception {
        log.info("대시보드 현장 월별 비용 집계 배치 시작");

        try {
            // 현재 시점 계산 (한국 시간 기준)
            final OffsetDateTime now = OffsetDateTime.now(AppConstants.KOREA_ZONE);
            final LocalDate currentDate = now.toLocalDate();
            final YearMonth currentYearMonth = YearMonth.from(currentDate);
            final OffsetDateTime threshold = now.minusMonths(1);

            // 대시보드 현장 목록 조회 (모든 현장 포함 - accessibleSiteIds를 null로 전달)
            final List<Site> sites = siteRepository.findSitesForDashboard(threshold, now, null);

            if (sites.isEmpty()) {
                log.info("대시보드 현장이 없습니다.");
                return;
            }

            log.info("대시보드 현장 {}건 발견", sites.size());

            int processedCount = 0;
            int errorCount = 0;

            // 각 현장별로 처리
            for (final Site site : sites) {
                try {
                    // 현장의 착공일(시작일) 확인
                    if (site.getStartedAt() == null) {
                        log.warn("현장 ID {}의 착공일이 없어 건너뜁니다.", site.getId());
                        continue;
                    }

                    final LocalDate startedDate = site.getStartedAt().toLocalDate();
                    final YearMonth startYearMonth = YearMonth.from(startedDate);

                    // 착공일이 포함된 월부터 현재 월까지 각 월별로 비용 집계
                    YearMonth targetMonth = startYearMonth;
                    while (!targetMonth.isAfter(currentYearMonth)) {
                        final String yearMonth = targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

                        // 현장의 각 공정별로 처리
                        for (final SiteProcess siteProcess : site.getProcesses()) {
                            if (siteProcess.isDeleted()) {
                                continue;
                            }

                            try {
                                processSiteProcessCost(site, siteProcess, yearMonth);
                                processedCount++;
                            } catch (final Exception e) {
                                log.error("현장 ID {}, 공정 ID {}, 년월 {} 처리 중 오류 발생: {}",
                                        site.getId(), siteProcess.getId(), yearMonth, e.getMessage(), e);
                                errorCount++;
                            }
                        }

                        targetMonth = targetMonth.plusMonths(1);
                    }

                } catch (final Exception e) {
                    log.error("현장 ID {} 처리 중 오류 발생: {}", site.getId(), e.getMessage(), e);
                    errorCount++;
                }
            }

            log.info("대시보드 현장 월별 비용 집계 배치 완료 - 처리된 건수: {}, 오류 건수: {}", processedCount, errorCount);

        } catch (final Exception e) {
            log.error("대시보드 현장 월별 비용 집계 배치 실행 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 현장 공정별 월별 비용 집계 처리
     */
    private void processSiteProcessCost(
            final Site site,
            final SiteProcess siteProcess,
            final String yearMonth) {

        // 각 비용별 집계 조회 및 총액 계산
        final Long materialCost = calculateMaterialCost(site.getId(), siteProcess.getId(), yearMonth);
        final Long laborCost = calculateLaborCost(site.getId(), siteProcess.getId(), yearMonth);
        final Long managementCost = calculateManagementCost(site.getId(), siteProcess.getId(), yearMonth);
        final Long equipmentCost = calculateEquipmentCost(site.getId(), siteProcess.getId(), yearMonth);
        final Long outsourcingCost = calculateOutsourcingCost(site.getId(), siteProcess.getId(), yearMonth);

        // 기존 데이터 조회 또는 생성
        SiteMonthlyCostSummary summary = siteMonthlyCostSummaryRepository
                .findByYearMonthAndSiteAndSiteProcess(yearMonth, site, siteProcess)
                .orElse(null);

        if (summary == null) {
            // 새로 생성
            summary = SiteMonthlyCostSummary.builder()
                    .yearMonth(yearMonth)
                    .site(site)
                    .siteProcess(siteProcess)
                    .build();
        }

        // 비용 업데이트
        summary.updateCosts(materialCost, laborCost, managementCost, equipmentCost, outsourcingCost);

        siteMonthlyCostSummaryRepository.save(summary);

        log.debug("현장 ID {}, 공정 ID {}, 년월 {} 비용 집계 완료 - 재료비: {}, 노무비: {}, 관리비: {}, 장비비: {}, 외주비: {}",
                site.getId(), siteProcess.getId(), yearMonth,
                materialCost, laborCost, managementCost, equipmentCost, outsourcingCost);
    }

    /**
     * 재료비 총액 계산
     */
    private Long calculateMaterialCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        try {
            final MaterialCostAggregationResponse response = materialCostAggregationService
                    .getMaterialCostAggregation(siteId, siteProcessId, yearMonth);

            long total = 0L;

            // 자재관리 총액 (해당 월만)
            if (response.materialManagements() != null) {
                for (final var item : response.materialManagements()) {
                    total += getTotal(item.currentBilling());
                }
            }

            // 강재수불부 총액 (해당 월만)
            if (response.steelManagements() != null) {
                for (final var item : response.steelManagements()) {
                    total += getTotal(item.currentBilling());
                }
            }

            // 유류집계 총액 (해당 월만)
            if (response.fuelAggregations() != null) {
                for (final var item : response.fuelAggregations()) {
                    total += getTotal(item.currentBilling());
                }
            }

            return total > 0 ? total : null;
        } catch (final Exception e) {
            log.warn("재료비 계산 중 오류 발생 - 현장 ID: {}, 공정 ID: {}, 년월: {}, 오류: {}",
                    siteId, siteProcessId, yearMonth, e.getMessage());
            return null;
        }
    }

    /**
     * 노무비 총액 계산
     */
    private Long calculateLaborCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        try {
            long total = 0L;

            // 정직원 노무비 (해당 월만)
            final LaborCostAggregationResponse regularResponse = laborCostAggregationService
                    .getLaborCostAggregation(siteId, siteProcessId, yearMonth, LaborType.REGULAR_EMPLOYEE);
            if (regularResponse.items() != null) {
                for (final var item : regularResponse.items()) {
                    total += getTotal(item.currentBilling());
                }
            }

            // 직영 노무비 (해당 월만)
            final LaborCostAggregationResponse directResponse = laborCostAggregationService
                    .getLaborCostAggregation(siteId, siteProcessId, yearMonth, LaborType.DIRECT_CONTRACT);
            if (directResponse.items() != null) {
                for (final var item : directResponse.items()) {
                    total += getTotal(item.currentBilling());
                }
            }

            return total > 0 ? total : null;
        } catch (final Exception e) {
            log.warn("노무비 계산 중 오류 발생 - 현장 ID: {}, 공정 ID: {}, 년월: {}, 오류: {}",
                    siteId, siteProcessId, yearMonth, e.getMessage());
            return null;
        }
    }

    /**
     * 관리비 총액 계산
     */
    private Long calculateManagementCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        try {
            final ManagementCostAggregationResponse response = managementCostAggregationService
                    .getManagementCostAggregation(
                            new ManagementCostAggregationRequest(siteId, siteProcessId, yearMonth));

            long total = 0L;

            if (response.items() != null) {
                for (final var item : response.items()) {
                    // 해당 월만 집계
                    total += getTotalFromManagementBilling(item.currentBilling());
                }
            }

            return total > 0 ? total : null;
        } catch (final Exception e) {
            log.warn("관리비 계산 중 오류 발생 - 현장 ID: {}, 공정 ID: {}, 년월: {}, 오류: {}",
                    siteId, siteProcessId, yearMonth, e.getMessage());
            return null;
        }
    }

    /**
     * 장비비 총액 계산
     */
    private Long calculateEquipmentCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        try {
            final EquipmentCostAggregationResponse response = equipmentCostAggregationService
                    .getEquipmentCostAggregation(siteId, siteProcessId, yearMonth);

            long total = 0L;

            if (response.items() != null) {
                for (final var item : response.items()) {
                    // 해당 월만 집계
                    total += getTotal(item.currentBilling());
                }
            }

            return total > 0 ? total : null;
        } catch (final Exception e) {
            log.warn("장비비 계산 중 오류 발생 - 현장 ID: {}, 공정 ID: {}, 년월: {}, 오류: {}",
                    siteId, siteProcessId, yearMonth, e.getMessage());
            return null;
        }
    }

    /**
     * 외주비 총액 계산
     */
    private Long calculateOutsourcingCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        try {
            final OutsourcingLaborCostAggregationResponse response = outsourcingLaborCostAggregationService
                    .getOutsourcingLaborCostAggregation(siteId, siteProcessId, yearMonth);

            long total = 0L;

            if (response.items() != null) {
                for (final var item : response.items()) {
                    // 해당 월만 집계
                    total += getTotal(item.currentBilling());
                }
            }

            return total > 0 ? total : null;
        } catch (final Exception e) {
            log.warn("외주비 계산 중 오류 발생 - 현장 ID: {}, 공정 ID: {}, 년월: {}, 오류: {}",
                    siteId, siteProcessId, yearMonth, e.getMessage());
            return null;
        }
    }

    /**
     * MaterialCostAggregationResponse의 BillingDetail 총액 반환 (null 처리)
     */
    private long getTotal(final BillingDetail billingDetail) {
        if (billingDetail == null) {
            return 0L;
        }
        return billingDetail.total() != null ? billingDetail.total() : 0L;
    }

    /**
     * ManagementCostAggregationResponse의 BillingDetail 총액 반환
     */
    private long getTotalFromManagementBilling(
            final ManagementCostAggregationResponse.BillingDetail billingDetail) {
        if (billingDetail == null) {
            return 0L;
        }
        return billingDetail.total();
    }
}

package com.lineinc.erp.api.server.infrastructure.config.batch.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.aggregation.constructionoutsourcing.service.ConstructionOutsourcingCompanyAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.equipmentcost.service.EquipmentCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.laborcost.service.LaborCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.managementcost.service.ManagementCostAggregationService;
import com.lineinc.erp.api.server.domain.aggregation.materialcost.service.MaterialCostAggregationService;
import com.lineinc.erp.api.server.domain.dashboard.entity.SiteMonthlyCostSummary;
import com.lineinc.erp.api.server.domain.dashboard.repository.SiteMonthlyCostSummaryRepository;
import com.lineinc.erp.api.server.domain.labor.enums.LaborType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ConstructionOutsourcingAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ConstructionOutsourcingAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.EquipmentCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.LaborCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MaterialCostAggregationResponse.MaterialManagementItemResponse.BillingDetail;
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
    private final ConstructionOutsourcingCompanyAggregationService constructionOutsourcingCompanyAggregationService;
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

            final AtomicInteger processedCount = new AtomicInteger(0);
            final AtomicInteger errorCount = new AtomicInteger(0);

            // 현장별 병렬 처리
            sites.parallelStream().forEach(site -> {
                try {
                    // 현장의 착공일(시작일) 확인
                    if (site.getStartedAt() == null) {
                        log.warn("현장 ID {}의 착공일이 없어 건너뜁니다.", site.getId());
                        return;
                    }

                    final LocalDate startedDate = site.getStartedAt().toLocalDate();
                    final YearMonth startYearMonth = YearMonth.from(startedDate);

                    // 착공일이 포함된 월부터 현재 월까지 각 월별로 비용 집계
                    YearMonth targetMonth = startYearMonth;
                    while (!targetMonth.isAfter(currentYearMonth)) {
                        final String yearMonth = targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

                        // 현장의 각 공정별로 병렬 처리
                        site.getProcesses().parallelStream()
                                .filter(process -> !process.isDeleted())
                                .forEach(siteProcess -> {
                                    try {
                                        processSiteProcessCost(site, siteProcess, yearMonth);
                                        processedCount.incrementAndGet();
                                    } catch (final Exception e) {
                                        log.error("현장 ID {}, 공정 ID {}, 년월 {} 처리 중 오류 발생: {}",
                                                site.getId(), siteProcess.getId(), yearMonth, e.getMessage(), e);
                                        errorCount.incrementAndGet();
                                    }
                                });

                        targetMonth = targetMonth.plusMonths(1);
                    }

                } catch (final Exception e) {
                    log.error("현장 ID {} 처리 중 오류 발생: {}", site.getId(), e.getMessage(), e);
                    errorCount.incrementAndGet();
                }
            });

            log.info("대시보드 현장 월별 비용 집계 배치 완료 - 처리된 건수: {}, 오류 건수: {}",
                    processedCount.get(), errorCount.get());

        } catch (final Exception e) {
            log.error("대시보드 현장 월별 비용 집계 배치 실행 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 현장 공정별 월별 비용 집계 처리
     * 각 비용 계산을 병렬로 수행하여 성능을 향상시킵니다.
     */
    private void processSiteProcessCost(
            final Site site,
            final SiteProcess siteProcess,
            final String yearMonth) {

        final Long siteId = site.getId();
        final Long siteProcessId = siteProcess.getId();

        // 각 비용별 집계를 병렬로 계산
        final CompletableFuture<Long> materialCostFuture = CompletableFuture
                .supplyAsync(() -> calculateCost("재료비", siteId, siteProcessId, yearMonth,
                        () -> calculateMaterialCost(siteId, siteProcessId, yearMonth)));

        final CompletableFuture<Long> laborCostFuture = CompletableFuture
                .supplyAsync(() -> calculateCost("노무비", siteId, siteProcessId, yearMonth,
                        () -> calculateLaborCost(siteId, siteProcessId, yearMonth)));

        final CompletableFuture<Long> managementCostFuture = CompletableFuture
                .supplyAsync(() -> calculateCost("관리비", siteId, siteProcessId, yearMonth,
                        () -> calculateManagementCost(siteId, siteProcessId, yearMonth)));

        final CompletableFuture<Long> equipmentCostFuture = CompletableFuture
                .supplyAsync(() -> calculateCost("장비비", siteId, siteProcessId, yearMonth,
                        () -> calculateEquipmentCost(siteId, siteProcessId, yearMonth)));

        final CompletableFuture<Long> outsourcingCostFuture = CompletableFuture
                .supplyAsync(() -> calculateCost("외주비", siteId, siteProcessId, yearMonth,
                        () -> calculateOutsourcingCost(siteId, siteProcessId, yearMonth)));

        // 모든 비용 계산 완료 대기
        final Long materialCost = materialCostFuture.join();
        final Long laborCost = laborCostFuture.join();
        final Long managementCost = managementCostFuture.join();
        final Long equipmentCost = equipmentCostFuture.join();
        final Long outsourcingCost = outsourcingCostFuture.join();

        // 기존 데이터 조회 또는 생성
        SiteMonthlyCostSummary summary = siteMonthlyCostSummaryRepository
                .findByYearMonthAndSiteAndSiteProcess(yearMonth, site, siteProcess)
                .orElse(null);

        if (summary == null) {
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
                siteId, siteProcessId, yearMonth,
                materialCost, laborCost, managementCost, equipmentCost, outsourcingCost);
    }

    /**
     * 비용 계산을 수행하고 예외를 처리합니다.
     */
    private Long calculateCost(
            final String costType,
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth,
            final Supplier<Long> calculator) {
        try {
            return calculator.get();
        } catch (final Exception e) {
            log.warn("{} 계산 중 오류 발생 - 현장 ID: {}, 공정 ID: {}, 년월: {}, 오류: {}",
                    costType, siteId, siteProcessId, yearMonth, e.getMessage());
            return null;
        }
    }

    /**
     * 재료비 총액 계산
     */
    private Long calculateMaterialCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        final MaterialCostAggregationResponse response = materialCostAggregationService
                .getMaterialCostAggregation(siteId, siteProcessId, yearMonth);

        long total = 0L;

        // 자재관리 총액 (해당 월만)
        total += sumBillingTotal(response.materialManagements(), item -> item.currentBilling());

        // 강재수불부 총액 (해당 월만)
        total += sumBillingTotal(response.steelManagements(), item -> item.currentBilling());

        // 유류집계 총액 (해당 월만)
        total += sumBillingTotal(response.fuelAggregations(), item -> item.currentBilling());

        return total;
    }

    /**
     * 노무비 총액 계산
     */
    private Long calculateLaborCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        long total = 0L;

        // 정직원 노무비 (해당 월만)
        final LaborCostAggregationResponse regularResponse = laborCostAggregationService
                .getLaborCostAggregation(siteId, siteProcessId, yearMonth, LaborType.REGULAR_EMPLOYEE);
        total += sumBillingTotal(regularResponse.items(), item -> item.currentBilling());

        // 직영 노무비 (해당 월만)
        final LaborCostAggregationResponse directResponse = laborCostAggregationService
                .getLaborCostAggregation(siteId, siteProcessId, yearMonth, LaborType.DIRECT_CONTRACT);
        total += sumBillingTotal(directResponse.items(), item -> item.currentBilling());

        return total;
    }

    /**
     * 관리비 총액 계산
     */
    private Long calculateManagementCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        final ManagementCostAggregationResponse response = managementCostAggregationService
                .getManagementCostAggregation(
                        new ManagementCostAggregationRequest(siteId, siteProcessId, yearMonth));

        return sumLongTotal(response.items(), item -> {
            final var billing = item.currentBilling();
            return billing != null ? billing.total() : 0L;
        });
    }

    /**
     * 장비비 총액 계산
     */
    private Long calculateEquipmentCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        final EquipmentCostAggregationResponse response = equipmentCostAggregationService
                .getEquipmentCostAggregation(siteId, siteProcessId, yearMonth);

        return sumBillingTotal(response.items(), item -> item.currentBilling());
    }

    /**
     * 외주비 총액 계산 (외주 공사 비용)
     */
    private Long calculateOutsourcingCost(final Long siteId, final Long siteProcessId, final String yearMonth) {
        final ConstructionOutsourcingAggregationResponse response = constructionOutsourcingCompanyAggregationService
                .getConstructionOutsourcingAggregation(
                        new ConstructionOutsourcingAggregationRequest(siteId, siteProcessId, yearMonth));

        return sumLongTotal(response.items(), item -> {
            final var billing = item.currentBilling();
            return billing != null ? billing.total() : 0L;
        });
    }

    /**
     * 리스트의 각 항목에서 BillingDetail을 추출하여 총액을 합산합니다.
     * 
     * @param items            리스트 (null 가능)
     * @param billingExtractor BillingDetail을 추출하는 함수
     * @return 총액
     */
    private <T> long sumBillingTotal(
            final List<T> items,
            final java.util.function.Function<T, BillingDetail> billingExtractor) {
        if (items == null) {
            return 0L;
        }
        return items.stream()
                .map(billingExtractor)
                .mapToLong(this::getBillingTotal)
                .sum();
    }

    /**
     * 리스트의 각 항목에서 Long 값을 추출하여 합산합니다.
     * 
     * @param items          리스트 (null 가능)
     * @param valueExtractor Long 값을 추출하는 함수
     * @return 총액
     */
    private <T> long sumLongTotal(
            final List<T> items,
            final java.util.function.Function<T, Long> valueExtractor) {
        if (items == null) {
            return 0L;
        }
        return items.stream()
                .map(valueExtractor)
                .mapToLong(value -> value != null ? value : 0L)
                .sum();
    }

    /**
     * BillingDetail의 총액을 반환합니다 (null 처리)
     */
    private long getBillingTotal(final BillingDetail billingDetail) {
        if (billingDetail == null) {
            return 0L;
        }
        return billingDetail.total() != null ? billingDetail.total() : 0L;
    }
}

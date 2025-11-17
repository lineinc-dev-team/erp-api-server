package com.lineinc.erp.api.server.domain.dashboard.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.batch.entity.BatchExecutionHistory;
import com.lineinc.erp.api.server.domain.batch.enums.BatchName;
import com.lineinc.erp.api.server.domain.batch.repository.BatchExecutionHistoryRepository;
import com.lineinc.erp.api.server.domain.dashboard.repository.SiteMonthlyCostSummaryRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.DashboardBatchExecutionTimeResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.DashboardSiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.SiteMonthlyCostResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.SiteMonthlyCostsResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

import lombok.RequiredArgsConstructor;

/**
 * 대시보드용 데이터 조회 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final UserService userService;
    private final SiteRepository siteRepository;
    private final SiteProcessService siteProcessService;
    private final SiteMonthlyCostSummaryRepository siteMonthlyCostSummaryRepository;
    private final BatchExecutionHistoryRepository batchExecutionHistoryRepository;

    public List<DashboardSiteResponse> getDashboardSites(final Long userId) {
        final OffsetDateTime now = OffsetDateTime.now(AppConstants.KOREA_ZONE);
        final OffsetDateTime threshold = now.minusMonths(1);
        final User user = userService.getUserByIdOrThrow(userId);
        // 접근 권한이 있는 현장만 조회 (본사직원이어도 각 사용자가 접근 권한을 가진 현장들만 반환)
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        final List<Site> sites = siteRepository.findSitesForDashboard(threshold, now, accessibleSiteIds);

        return sites.stream()
                .map(DashboardSiteResponse::from)
                .toList();
    }

    /**
     * 현장별 월별 비용 총합 조회 (본사직원만 가능)
     * 본사직원이어도 각 사용자가 접근 권한을 가진 현장들의 월별 비용 총합을 반환합니다.
     * 각 현장의 모든 공정을 합산한 월별 비용을 반환합니다.
     */
    public List<SiteMonthlyCostsResponse> getSiteMonthlyCosts(final Long userId) {
        final User user = userService.getUserByIdOrThrow(userId);

        // 본사직원 체크
        if (!user.isHeadOffice()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ValidationMessages.ACCESS_DENIED);
        }

        // 대시보드 현장 목록 조회 (접근 권한이 있는 현장만)
        // 본사직원이어도 각 사용자가 접근 권한을 가진 현장들만 반환
        final OffsetDateTime now = OffsetDateTime.now(AppConstants.KOREA_ZONE);
        final OffsetDateTime threshold = now.minusMonths(1);
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);
        final List<Site> sites = siteRepository.findSitesForDashboard(threshold, now, accessibleSiteIds);

        return sites.stream()
                .map(site -> {
                    // 해당 현장의 모든 공정을 합산한 월별 비용 조회
                    final List<Object[]> monthlyCostsData = siteMonthlyCostSummaryRepository
                            .findMonthlyCostsBySiteId(site.getId());

                    // 모든 월의 비용을 합산하여 총합 계산
                    long totalMaterialCost = 0L;
                    long totalLaborCost = 0L;
                    long totalManagementCost = 0L;
                    long totalEquipmentCost = 0L;
                    long totalOutsourcingCost = 0L;

                    for (final Object[] data : monthlyCostsData) {
                        totalMaterialCost += ((Number) data[1]).longValue();
                        totalLaborCost += ((Number) data[2]).longValue();
                        totalManagementCost += ((Number) data[3]).longValue();
                        totalEquipmentCost += ((Number) data[4]).longValue();
                        totalOutsourcingCost += ((Number) data[5]).longValue();
                    }

                    final long totalCost = totalMaterialCost + totalLaborCost + totalManagementCost
                            + totalEquipmentCost + totalOutsourcingCost;

                    return new SiteMonthlyCostsResponse(
                            SiteResponse.SiteSimpleResponse.from(site),
                            SiteProcessResponse.SiteProcessSimpleResponse.from(site.getProcesses().get(0)),
                            totalMaterialCost,
                            totalLaborCost,
                            totalManagementCost,
                            totalEquipmentCost,
                            totalOutsourcingCost,
                            totalCost);
                })
                .collect(Collectors.toList());
    }

    /**
     * 현장 및 공정별 월별 비용 목록 조회
     * 해당 현장에 접근 권한이 있는 사용자만 조회 가능합니다.
     */
    public List<SiteMonthlyCostResponse> getSiteProcessMonthlyCosts(
            final Long userId,
            final Long siteId,
            final Long siteProcessId) {
        final User user = userService.getUserByIdOrThrow(userId);

        // 현장 존재 확인
        siteRepository.findById(siteId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));

        // 현장 접근 권한 체크
        final List<Long> accessibleSiteIds = userService.getAccessibleSiteIds(user);

        // 본사직원이거나 접근 가능한 현장 목록에 포함되어 있어야 함
        if (accessibleSiteIds != null && !accessibleSiteIds.contains(siteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ValidationMessages.ACCESS_DENIED);
        }

        // 현장 및 공정 조회
        final Site site = siteRepository.findById(siteId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.SITE_NOT_FOUND));
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(siteProcessId);

        // 월별 비용 조회
        final List<Object[]> monthlyCostsData = siteMonthlyCostSummaryRepository
                .findMonthlyCostsBySiteIdAndSiteProcessId(siteId, siteProcessId);

        return monthlyCostsData.stream()
                .map(data -> {
                    final String yearMonth = (String) data[0];
                    final Long materialCost = ((Number) data[1]).longValue();
                    final Long laborCost = ((Number) data[2]).longValue();
                    final Long managementCost = ((Number) data[3]).longValue();
                    final Long equipmentCost = ((Number) data[4]).longValue();
                    final Long outsourcingCost = ((Number) data[5]).longValue();

                    return SiteMonthlyCostResponse.from(
                            site,
                            siteProcess,
                            yearMonth,
                            materialCost,
                            laborCost,
                            managementCost,
                            equipmentCost,
                            outsourcingCost);
                })
                .collect(Collectors.toList());
    }

    /**
     * 배치의 가장 최근 실행 시간 조회
     * 
     * @param batchName 배치 이름 Enum
     * @return 배치 실행 종료 시간 (없으면 null)
     */
    public DashboardBatchExecutionTimeResponse getBatchExecutionTime(final BatchName batchName) {
        final String batchNameLabel = batchName.getLabel();
        final OffsetDateTime latestEndTime = batchExecutionHistoryRepository
                .findTop1ByBatchNameAndEndTimeIsNotNullOrderByEndTimeDesc(batchNameLabel)
                .map(BatchExecutionHistory::getEndTime)
                .orElse(null);
        return new DashboardBatchExecutionTimeResponse(latestEndTime);
    }
}

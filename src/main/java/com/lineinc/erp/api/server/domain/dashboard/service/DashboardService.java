package com.lineinc.erp.api.server.domain.dashboard.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.dashboard.repository.SiteMonthlyCostSummaryRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.DashboardSiteResponse;
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
    private final SiteMonthlyCostSummaryRepository siteMonthlyCostSummaryRepository;

    public List<DashboardSiteResponse> getDashboardSites(final Long userId) {
        final OffsetDateTime now = OffsetDateTime.now(AppConstants.KOREA_ZONE);
        final OffsetDateTime threshold = now.minusMonths(1);
        final User user = userService.getUserByIdOrThrow(userId);
        final List<Long> accessibleSiteIds = resolveAccessibleSiteIds(user);
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

    private List<Long> resolveAccessibleSiteIds(final User user) {
        if (user.isHeadOffice()) {
            return null;
        }
        return userService.getAccessibleSiteIds(user);
    }
}

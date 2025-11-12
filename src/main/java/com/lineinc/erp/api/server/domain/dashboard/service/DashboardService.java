package com.lineinc.erp.api.server.domain.dashboard.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.service.v1.UserService;
import com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response.DashboardSiteResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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

    private List<Long> resolveAccessibleSiteIds(final User user) {
        if (user.isHeadOffice()) {
            return null;
        }
        return userService.getAccessibleSiteIds(user);
    }
}

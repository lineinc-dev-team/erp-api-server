package com.lineinc.erp.api.server.domain.site.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

public interface SiteRepositoryCustom {
    /**
     * 동적 조건으로 현장 목록을 조회합니다.
     */
    Page<SiteResponse> findAll(
            SiteListRequest request,
            Pageable pageable,
            List<Long> accessibleSiteIds);

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 현장 목록 전체 조회
     */
    List<Site> findAllWithoutPaging(
            SiteListRequest request,
            Sort sort,
            List<Long> accessibleSiteIds);

    /**
     * 대시보드에서 사용할 진행/최근 종료 현장 목록을 조회합니다.
     */
    List<Site> findSitesForDashboard(
            OffsetDateTime endedAtThreshold,
            OffsetDateTime currentDateTime,
            List<Long> accessibleSiteIds);
}

package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.site.SiteResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SiteRepositoryCustom {
    /**
     * 동적 조건으로 현장 목록을 조회합니다.
     */
    Page<SiteResponse> findAll(
            SiteListRequest request,
            Pageable pageable,
            List<Long> accessibleSiteIds
    );

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 현장 목록 전체 조회
     */
    List<Site> findAllWithoutPaging(
            SiteListRequest request,
            Sort sort,
            List<Long> accessibleSiteIds
    );
}

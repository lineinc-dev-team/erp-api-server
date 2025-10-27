package com.lineinc.erp.api.server.domain.steelmanagementv2.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2ListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2Response;

public interface SteelManagementV2RepositoryCustom {
    /**
     * 동적 조건으로 강재수불부 V2 목록을 조회합니다.
     */
    Page<SteelManagementV2Response> findAll(
            SteelManagementV2ListRequest request,
            Pageable pageable,
            List<Long> accessibleSiteIds);

    /**
     * 동적 조건으로 강재수불부 V2 목록을 조회합니다 (페이징 없음, 엑셀 다운로드용).
     */
    List<SteelManagementV2Response> findAllWithoutPaging(
            SteelManagementV2ListRequest request,
            Sort sort,
            List<Long> accessibleSiteIds);

    /**
     * 현장, 공정, 조회월 이하로 강재수불부 V2 목록을 조회합니다.
     * 
     * @param site        현장
     * @param siteProcess 공정
     * @param yearMonth   조회월 (YYYY-MM)
     * @return 강재수불부 V2 목록
     */
    List<SteelManagementV2> findBySiteAndSiteProcessAndYearMonthLessThanEqual(
            Site site,
            SiteProcess siteProcess,
            String yearMonth);
}

package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementResponse;

public interface MaterialManagementRepositoryCustom {
    /**
     * 동적 조건으로 자재 관리 목록을 조회합니다.
     */
    Page<MaterialManagementResponse> findAll(
            MaterialManagementListRequest request,
            Pageable pageable,
            List<Long> accessibleSiteIds);

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 자재 관리 목록 전체 조회
     */
    List<MaterialManagementResponse> findAllWithoutPaging(
            MaterialManagementListRequest request,
            Sort sort,
            List<Long> accessibleSiteIds);

    /**
     * 현장, 공정, 조회월 이하로 자재 관리 목록을 조회합니다.
     * 
     * @param site        현장
     * @param siteProcess 공정
     * @param yearMonth   조회월 (YYYY-MM)
     * @return 자재 관리 목록
     */
    List<MaterialManagement> findBySiteAndSiteProcessAndYearMonthLessThanEqual(
            Site site,
            SiteProcess siteProcess,
            String yearMonth);
}

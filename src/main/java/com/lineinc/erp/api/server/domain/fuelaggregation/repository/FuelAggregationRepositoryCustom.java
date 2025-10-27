package com.lineinc.erp.api.server.domain.fuelaggregation.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationListResponse;

public interface FuelAggregationRepositoryCustom {
    /**
     * 동적 조건으로 유류집계 목록을 조회합니다.
     */
    Page<FuelAggregationListResponse> findAll(
            FuelAggregationListRequest request,
            Pageable pageable,
            List<Long> accessibleSiteIds);

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 유류집계 목록 전체 조회
     */
    List<FuelAggregationListResponse> findAllWithoutPaging(
            FuelAggregationListRequest request,
            Sort sort,
            List<Long> accessibleSiteIds);

    /**
     * 현장, 공정, 조회월 이하로 유류집계 목록을 조회합니다.
     * 
     * @param site        현장
     * @param siteProcess 공정
     * @param yearMonth   조회월 (YYYY-MM)
     * @return 유류집계 목록
     */
    List<FuelAggregation> findBySiteAndSiteProcessAndYearMonthLessThanEqual(
            Site site,
            SiteProcess siteProcess,
            String yearMonth);
}

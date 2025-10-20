package com.lineinc.erp.api.server.domain.dailyreport.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportListSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportListResponse;

public interface DailyReportRepositoryCustom {

    /**
     * 출역일보 목록 조회 (동적 검색 조건 지원)
     */
    Page<DailyReportListResponse> findAllBySearchConditions(DailyReportListSearchRequest request, Pageable pageable,
            List<Long> accessibleSiteIds);
}

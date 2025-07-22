package com.lineinc.erp.api.server.domain.site.repository;


import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SiteRepositoryCustom {
    /**
     * 동적 조건으로 현장 목록을 조회합니다.
     */
    Page<SiteResponse> findAll(
            SiteListRequest request,
            Pageable pageable
    );


}

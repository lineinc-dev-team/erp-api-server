package com.lineinc.erp.api.server.domain.sitemanagementcost.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostResponse;

/**
 * 현장관리비 커스텀 Repository 인터페이스
 */
public interface SiteManagementCostRepositoryCustom {

    /**
     * 현장관리비 목록 조회 (페이징)
     */
    Page<SiteManagementCostResponse> findAll(SiteManagementCostListRequest request, Pageable pageable);
}

package com.lineinc.erp.api.server.domain.steelmanagementv2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2ListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2Response;

public interface SteelManagementV2RepositoryCustom {
    /**
     * 동적 조건으로 강재수불부 V2 목록을 조회합니다.
     */
    Page<SteelManagementV2Response> findAll(
            SteelManagementV2ListRequest request,
            Pageable pageable);
}

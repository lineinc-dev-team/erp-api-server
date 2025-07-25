package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.response.SteelManagementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SteelManagementRepositoryCustom {
    /**
     * 동적 조건으로 강재 관리 목록을 조회합니다.
     */
    Page<SteelManagementResponse> findAll(
            SteelManagementListRequest request,
            Pageable pageable
    );

}

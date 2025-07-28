package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementListRequest;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.response.MaterialManagementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialManagementRepositoryCustom {
    /**
     * 동적 조건으로 자재 관리 목록을 조회합니다.
     */
    Page<MaterialManagementResponse> findAll(
            MaterialManagementListRequest request,
            Pageable pageable
    );
}

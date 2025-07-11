package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RolesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleRepositoryCustom {

    /**
     * 동적 조건으로 권한 목록을 조회합니다.
     */
    Page<RolesResponse> findAll(
            Object request,
            Pageable pageable
    );
}

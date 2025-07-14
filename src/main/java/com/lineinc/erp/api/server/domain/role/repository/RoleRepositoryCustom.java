package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.presentation.v1.role.dto.request.RoleUserListRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.UserWithRolesListRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RoleUserListResponse;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RolesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleRepositoryCustom {

    /**
     * 동적 조건으로 권한 목록을 조회합니다.
     */
    Page<RolesResponse> findAll(
            UserWithRolesListRequest request,
            Pageable pageable
    );

    /**
     * 특정 권한 그룹에 속한 사용자 목록을 검색어로 조회합니다.
     */
    Page<RoleUserListResponse> findUsersByRoleId(
            Long roleId,
            RoleUserListRequest request,
            Pageable pageable);
}

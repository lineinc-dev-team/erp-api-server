package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserInfoResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UserListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

    /**
     * 동적 조건으로 유저 목록을 조회합니다.
     */
    Page<UserInfoResponse> findAll(
            UserListRequest request,
            Pageable pageable
    );

}
package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UserListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface UserRepositoryCustom {

    /**
     * 동적 조건으로 유저 목록을 조회합니다.
     */
    Page<UserResponse> findAll(
            UserListRequest request,
            Pageable pageable
    );

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 유저 목록 전체 조회
     */
    List<UserResponse> findAllWithoutPaging(
            UserListRequest request,
            Sort sort
    );
}
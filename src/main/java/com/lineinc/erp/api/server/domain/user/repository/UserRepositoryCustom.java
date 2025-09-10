package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.SearchUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * 사용자 Repository 커스텀 인터페이스
 * 복잡한 동적 쿼리를 위한 QueryDSL 기반 메서드들
 */
public interface UserRepositoryCustom {

    /**
     * 동적 조건으로 사용자 목록을 조회합니다.
     * 
     * @param request  검색 조건
     * @param pageable 페이징 정보
     * @return 사용자 목록 (페이징)
     */
    Page<UserResponse> findAll(SearchUserRequest request, Pageable pageable);

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 사용자 목록 전체 조회
     * 
     * @param request 검색 조건
     * @param sort    정렬 정보
     * @return 사용자 목록 (전체)
     */
    List<UserResponse> findAllWithoutPaging(SearchUserRequest request, Sort sort);
}

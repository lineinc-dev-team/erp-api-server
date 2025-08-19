package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementResponse;

public interface MaterialManagementRepositoryCustom {
    /**
     * 동적 조건으로 자재 관리 목록을 조회합니다.
     */
    Page<MaterialManagementResponse> findAll(
            MaterialManagementListRequest request,
            Pageable pageable);

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 자재 관리 목록 전체 조회
     */
    List<MaterialManagementResponse> findAllWithoutPaging(
            MaterialManagementListRequest request,
            Sort sort);
}

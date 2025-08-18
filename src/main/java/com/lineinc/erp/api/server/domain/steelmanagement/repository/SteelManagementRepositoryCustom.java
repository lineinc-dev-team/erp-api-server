package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementResponse;

public interface SteelManagementRepositoryCustom {
    /**
     * 동적 조건으로 강재 관리 목록을 조회합니다.
     */
    Page<SteelManagementResponse> findAll(
            SteelManagementListRequest request,
            Pageable pageable);

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 강재 관리 목록 전체 조회
     */
    List<SteelManagement> findAllWithoutPaging(
            SteelManagementListRequest request,
            Sort sort);
}

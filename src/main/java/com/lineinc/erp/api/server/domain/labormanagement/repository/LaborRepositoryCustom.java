package com.lineinc.erp.api.server.domain.labormanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborListResponse;

public interface LaborRepositoryCustom {

    /**
     * 인력정보 목록을 조건에 따라 조회합니다.
     */
    Page<LaborListResponse> findAll(LaborListRequest request, Pageable pageable);

    /**
     * 페이지네이션 없이 인력정보 목록을 조건에 따라 조회합니다. (엑셀 다운로드용)
     */
    List<LaborListResponse> findAllWithoutPaging(LaborListRequest request, Sort sort);
}

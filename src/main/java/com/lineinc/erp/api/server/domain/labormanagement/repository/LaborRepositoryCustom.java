package com.lineinc.erp.api.server.domain.labormanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborListResponse;

public interface LaborRepositoryCustom {
    
    /**
     * 인력정보 목록을 조건에 따라 조회합니다.
     */
    Page<LaborListResponse> findAll(LaborListRequest request, Pageable pageable);
}

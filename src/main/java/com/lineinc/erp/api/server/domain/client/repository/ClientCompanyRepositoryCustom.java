package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientCompanyRepositoryCustom {

    /**
     * 동적 조건으로 발주처 목록을 조회합니다.
     */
    Page<ClientCompanyResponse> findAll(
            ClientCompanyListRequest request,
            Pageable pageable
    );
}
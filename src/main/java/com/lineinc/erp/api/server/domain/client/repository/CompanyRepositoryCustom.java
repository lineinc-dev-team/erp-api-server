package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface CompanyRepositoryCustom {

    /**
     * 동적 조건으로 발주처 목록을 조회합니다.
     */
    Page<ClientCompanyResponse> findAll(
            ClientCompanyListRequest request,
            Pageable pageable
    );

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 발주처 목록 전체 조회
     */
    List<ClientCompany> findAllWithoutPaging(
            ClientCompanyListRequest request,
            Sort sort
    );
}
package com.lineinc.erp.api.server.domain.outsourcingcompany.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

public interface OutsourcingCompanyRepositoryCustom {

    /**
     * 동적 조건으로 발주처 목록을 조회합니다.
     */
    Page<CompanyResponse> findAll(
            OutsourcingCompanyListRequest request,
            Pageable pageable);

    /**
     * 엑셀 다운로드용 - 페이지네이션 없이 발주처 목록 전체 조회
     */
    List<OutsourcingCompany> findAllWithoutPaging(
            OutsourcingCompanyListRequest request,
            Sort sort);
}
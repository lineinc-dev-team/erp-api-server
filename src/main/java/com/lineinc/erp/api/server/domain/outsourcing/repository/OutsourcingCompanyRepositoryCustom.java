package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.OutsourcingCompanyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OutsourcingCompanyRepositoryCustom {

    /**
     * 동적 조건으로 발주처 목록을 조회합니다.
     */
    Page<OutsourcingCompanyResponse> findAll(
            OutsourcingCompanyListRequest request,
            Pageable pageable
    );

//    /**
//     * 엑셀 다운로드용 - 페이지네이션 없이 발주처 목록 전체 조회
//     */
//    List<ClientCompany> findAllWithoutPaging(
//            ClientCompanyListRequest request,
//            Sort sort
//    );
}
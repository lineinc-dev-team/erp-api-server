package com.lineinc.erp.api.server.domain.outsourcing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.ContractListSearchRequest;

public interface OutsourcingCompanyContractRepositoryCustom {

    /**
     * 검색 조건에 따라 외주계약 리스트를 페이징하여 조회합니다.
     */
    Page<com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract> findBySearchConditions(
            ContractListSearchRequest searchRequest,
            Pageable pageable);
}

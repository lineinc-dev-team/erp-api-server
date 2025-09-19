package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.ContractListSearchRequest;

public interface OutsourcingCompanyContractRepositoryCustom {

    /**
     * 검색 조건에 따라 외주계약 리스트를 페이징하여 조회합니다.
     */
    Page<OutsourcingCompanyContract> findBySearchConditions(
            ContractListSearchRequest searchRequest,
            Pageable pageable);

    /**
     * 검색 조건에 따라 외주계약 리스트를 페이징 없이 조회합니다.
     */
    List<OutsourcingCompanyContract> findAllWithoutPaging(
            ContractListSearchRequest searchRequest,
            Sort sort);
}

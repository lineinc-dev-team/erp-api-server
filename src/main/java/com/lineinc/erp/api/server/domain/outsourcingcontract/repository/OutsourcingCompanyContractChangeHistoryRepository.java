package com.lineinc.erp.api.server.domain.outsourcingcontract.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractChangeHistory;

@Repository
public interface OutsourcingCompanyContractChangeHistoryRepository
        extends CrudRepository<OutsourcingCompanyContractChangeHistory, Long> {

    /**
     * 계약 ID로 변경 이력을 페이징하여 조회합니다.
     */
    Slice<OutsourcingCompanyContractChangeHistory> findByOutsourcingCompanyContractId(
            Long contractId, Pageable pageable);
}

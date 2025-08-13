package com.lineinc.erp.api.server.domain.outsourcing.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractChangeHistory;

@Repository
public interface OutsourcingCompanyContractChangeHistoryRepository
        extends CrudRepository<OutsourcingCompanyContractChangeHistory, Long> {
}

package com.lineinc.erp.api.server.domain.outsourcing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractHistory;

@Repository
public interface OutsourcingCompanyContractHistoryRepository
        extends JpaRepository<OutsourcingCompanyContractHistory, Long> {
}

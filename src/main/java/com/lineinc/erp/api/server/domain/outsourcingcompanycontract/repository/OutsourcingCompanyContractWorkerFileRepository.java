package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorkerFile;

@Repository
public interface OutsourcingCompanyContractWorkerFileRepository
        extends JpaRepository<OutsourcingCompanyContractWorkerFile, Long> {
}

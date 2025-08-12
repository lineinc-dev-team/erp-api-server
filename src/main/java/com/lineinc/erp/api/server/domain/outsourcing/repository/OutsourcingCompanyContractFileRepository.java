package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutsourcingCompanyContractFileRepository extends JpaRepository<OutsourcingCompanyContractFile, Long> {
}

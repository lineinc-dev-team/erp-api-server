package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutsourcingCompanyContractDriverRepository extends JpaRepository<OutsourcingCompanyContractDriver, Long> {
}

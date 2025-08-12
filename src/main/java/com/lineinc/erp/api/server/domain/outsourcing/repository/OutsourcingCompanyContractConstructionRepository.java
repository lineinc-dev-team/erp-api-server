package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractConstruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutsourcingCompanyContractConstructionRepository extends JpaRepository<OutsourcingCompanyContractConstruction, Long> {
}

package com.lineinc.erp.api.server.domain.outsourcing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;

@Repository
public interface OutsourcingCompanyContractRepository
                extends JpaRepository<OutsourcingCompanyContract, Long>, OutsourcingCompanyContractRepositoryCustom {

}

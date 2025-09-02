package com.lineinc.erp.api.server.domain.outsourcingcontract.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContract;

@Repository
public interface OutsourcingCompanyContractRepository
        extends JpaRepository<OutsourcingCompanyContract, Long>, OutsourcingCompanyContractRepositoryCustom {

    List<OutsourcingCompanyContract> findByOutsourcingCompanyId(Long outsourcingCompanyId);
}

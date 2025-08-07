package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutsourcingCompanyRepository extends JpaRepository<OutsourcingCompany, Long>, OutsourcingCompanyRepositoryCustom {
    boolean existsByBusinessNumber(String businessNumber);
}

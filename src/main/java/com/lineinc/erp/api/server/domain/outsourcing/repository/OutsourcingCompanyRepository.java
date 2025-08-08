package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface OutsourcingCompanyRepository extends JpaRepository<OutsourcingCompany, Long>, OutsourcingCompanyRepositoryCustom {
    boolean existsByBusinessNumber(String businessNumber);

    Slice<OutsourcingCompany> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Slice<OutsourcingCompany> findAllBy(Pageable pageable);
}

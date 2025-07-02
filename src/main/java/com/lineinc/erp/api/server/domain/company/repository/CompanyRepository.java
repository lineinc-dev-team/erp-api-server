package com.lineinc.erp.api.server.domain.company.repository;

import com.lineinc.erp.api.server.domain.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}

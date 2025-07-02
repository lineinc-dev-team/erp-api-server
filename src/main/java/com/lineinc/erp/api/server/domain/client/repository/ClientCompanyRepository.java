package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientCompanyRepository extends JpaRepository<ClientCompany, Long> {
}

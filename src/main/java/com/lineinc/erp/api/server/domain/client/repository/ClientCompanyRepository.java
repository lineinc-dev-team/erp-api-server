package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientCompanyRepository extends JpaRepository<ClientCompany, Long>, ClientCompanyRepositoryCustom {
    Slice<ClientCompany> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Slice<ClientCompany> findAllBy(Pageable pageable);
}

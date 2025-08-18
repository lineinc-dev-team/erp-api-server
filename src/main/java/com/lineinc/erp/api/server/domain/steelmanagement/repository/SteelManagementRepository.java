package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;

@Repository
public interface SteelManagementRepository
        extends JpaRepository<SteelManagement, Long>, SteelManagementRepositoryCustom {
}

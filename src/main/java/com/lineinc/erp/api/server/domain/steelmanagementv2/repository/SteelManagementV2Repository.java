package com.lineinc.erp.api.server.domain.steelmanagementv2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;

/**
 * 강재수불부 V2 Repository
 */
@Repository
public interface SteelManagementV2Repository extends JpaRepository<SteelManagementV2, Long> {
}

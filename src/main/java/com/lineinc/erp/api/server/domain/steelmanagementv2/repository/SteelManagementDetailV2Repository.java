package com.lineinc.erp.api.server.domain.steelmanagementv2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementDetailV2;

/**
 * 강재수불부 V2 상세 Repository
 */
@Repository
public interface SteelManagementDetailV2Repository extends JpaRepository<SteelManagementDetailV2, Long> {
}

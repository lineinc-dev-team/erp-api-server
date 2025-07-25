package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteelManagementFileRepository extends JpaRepository<SteelManagementFile, Long> {
}

package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialManagementFileRepository extends JpaRepository<MaterialManagementFile, Long> {
}

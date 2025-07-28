package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialManagementDetailRepository extends JpaRepository<MaterialManagementDetail, Long> {
}

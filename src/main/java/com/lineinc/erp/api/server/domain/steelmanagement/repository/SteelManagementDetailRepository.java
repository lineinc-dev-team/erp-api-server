package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteelManagementDetailRepository extends JpaRepository<SteelManagementDetail, Long> {
}

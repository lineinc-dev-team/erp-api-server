package com.lineinc.erp.api.server.domain.managementcost.repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementCostDetailRepository extends JpaRepository<ManagementCostDetail, Long> {
}

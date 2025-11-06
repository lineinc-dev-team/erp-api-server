package com.lineinc.erp.api.server.domain.managementcost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailEquipment;

@Repository
public interface ManagementCostMealFeeDetailEquipmentRepository
        extends JpaRepository<ManagementCostMealFeeDetailEquipment, Long> {
}

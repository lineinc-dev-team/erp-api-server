package com.lineinc.erp.api.server.domain.managementcost.repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailDirectContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementCostMealFeeDetailDirectContractRepository
        extends JpaRepository<ManagementCostMealFeeDetailDirectContract, Long> {
}


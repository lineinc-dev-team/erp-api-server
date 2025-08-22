package com.lineinc.erp.api.server.domain.managementcost.repository;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementCostChangeHistoryRepository extends JpaRepository<ManagementCostChangeHistory, Long> {

    /**
     * 관리비의 변경 이력 목록을 조회합니다.
     */
    Slice<ManagementCostChangeHistory> findAllByManagementCost(ManagementCost managementCost, Pageable pageable);
}

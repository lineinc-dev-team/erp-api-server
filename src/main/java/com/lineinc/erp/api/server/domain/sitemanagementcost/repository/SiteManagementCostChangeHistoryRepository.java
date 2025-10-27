package com.lineinc.erp.api.server.domain.sitemanagementcost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCostChangeHistory;

/**
 * 현장관리비 변경 이력 Repository
 */
@Repository
public interface SiteManagementCostChangeHistoryRepository
        extends JpaRepository<SiteManagementCostChangeHistory, Long> {
}

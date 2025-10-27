package com.lineinc.erp.api.server.domain.sitemanagementcost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost;

/**
 * 월별 현장관리비 Repository
 */
@Repository
public interface SiteManagementCostRepository
        extends JpaRepository<SiteManagementCost, Long> {

}

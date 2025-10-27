package com.lineinc.erp.api.server.domain.sitemanagementcost.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCostChangeHistory;

/**
 * 현장관리비 변경 이력 Repository
 */
@Repository
public interface SiteManagementCostChangeHistoryRepository
        extends JpaRepository<SiteManagementCostChangeHistory, Long> {

    /**
     * 현장관리비 ID로 변경 이력을 페이징하여 조회합니다.
     * 
     * @param siteManagementCostId 현장관리비 ID
     * @param pageable             페이징 정보
     * @return 현장관리비 변경 이력 페이지
     */
    @Query("SELECT smcch FROM SiteManagementCostChangeHistory smcch WHERE smcch.siteManagementCost.id = :siteManagementCostId")
    Page<SiteManagementCostChangeHistory> findBySiteManagementCostIdWithPaging(
            @Param("siteManagementCostId") Long siteManagementCostId,
            Pageable pageable);
}

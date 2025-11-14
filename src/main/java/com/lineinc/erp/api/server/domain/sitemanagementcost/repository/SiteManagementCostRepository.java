package com.lineinc.erp.api.server.domain.sitemanagementcost.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost;

/**
 * 월별 현장관리비 Repository
 */
@Repository
public interface SiteManagementCostRepository
        extends JpaRepository<SiteManagementCost, Long>, SiteManagementCostRepositoryCustom {

    /**
     * 년월, 현장, 공정으로 현장관리비 존재 여부 확인
     */
    @Query("""
            SELECT smc FROM SiteManagementCost smc \
            WHERE smc.yearMonth = :yearMonth \
            AND smc.site = :site \
            AND smc.siteProcess = :siteProcess \
            AND smc.deleted = false""")
    Optional<SiteManagementCost> findByYearMonthAndSiteAndSiteProcess(
            @Param("yearMonth") String yearMonth,
            @Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess);

    /**
     * 조회월 이전의 모든 현장관리비 조회 (전회 집계용)
     */
    @Query("""
            SELECT smc FROM SiteManagementCost smc \
            WHERE smc.yearMonth < :yearMonth \
            AND smc.site = :site \
            AND smc.siteProcess = :siteProcess \
            AND smc.deleted = false \
            ORDER BY smc.yearMonth""")
    List<SiteManagementCost> findByYearMonthLessThanAndSiteAndSiteProcess(
            @Param("yearMonth") String yearMonth,
            @Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess);
}

package com.lineinc.erp.api.server.domain.dashboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.dashboard.entity.SiteMonthlyCostSummary;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

/**
 * 현장별 월별 비용 집계 Repository
 */
@Repository
public interface SiteMonthlyCostSummaryRepository extends JpaRepository<SiteMonthlyCostSummary, Long> {

    /**
     * 년월, 현장, 공정으로 비용 집계 조회
     */
    @Query("""
            SELECT smcs FROM SiteMonthlyCostSummary smcs \
            WHERE smcs.yearMonth = :yearMonth \
            AND smcs.site = :site \
            AND smcs.siteProcess = :siteProcess \
            AND smcs.deleted = false""")
    Optional<SiteMonthlyCostSummary> findByYearMonthAndSiteAndSiteProcess(
            @Param("yearMonth") String yearMonth,
            @Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess);

    /**
     * 현장별 월별 비용 집계 조회 (현장의 모든 공정 합계)
     */
    @Query("""
            SELECT smcs.yearMonth,
                   SUM(COALESCE(smcs.materialCost, 0)) as materialCost,
                   SUM(COALESCE(smcs.laborCost, 0)) as laborCost,
                   SUM(COALESCE(smcs.managementCost, 0)) as managementCost,
                   SUM(COALESCE(smcs.equipmentCost, 0)) as equipmentCost,
                   SUM(COALESCE(smcs.outsourcingCost, 0)) as outsourcingCost
            FROM SiteMonthlyCostSummary smcs
            WHERE smcs.site.id = :siteId
            AND smcs.deleted = false
            GROUP BY smcs.yearMonth
            ORDER BY smcs.yearMonth""")
    List<Object[]> findMonthlyCostsBySiteId(@Param("siteId") Long siteId);

    /**
     * 전체 현장의 월별 비용 총합 조회 (접근 권한이 있는 현장들만)
     */
    @Query("""
            SELECT smcs.yearMonth,
                   SUM(COALESCE(smcs.materialCost, 0)) as materialCost,
                   SUM(COALESCE(smcs.laborCost, 0)) as laborCost,
                   SUM(COALESCE(smcs.managementCost, 0)) as managementCost,
                   SUM(COALESCE(smcs.equipmentCost, 0)) as equipmentCost,
                   SUM(COALESCE(smcs.outsourcingCost, 0)) as outsourcingCost
            FROM SiteMonthlyCostSummary smcs
            WHERE smcs.site.id IN :siteIds
            AND smcs.deleted = false
            GROUP BY smcs.yearMonth
            ORDER BY smcs.yearMonth""")
    List<Object[]> findMonthlyCostsTotalBySiteIds(@Param("siteIds") List<Long> siteIds);

    /**
     * 현장 및 공정별 월별 비용 목록 조회
     */
    @Query("""
            SELECT smcs.yearMonth,
                   COALESCE(smcs.materialCost, 0) as materialCost,
                   COALESCE(smcs.laborCost, 0) as laborCost,
                   COALESCE(smcs.managementCost, 0) as managementCost,
                   COALESCE(smcs.equipmentCost, 0) as equipmentCost,
                   COALESCE(smcs.outsourcingCost, 0) as outsourcingCost
            FROM SiteMonthlyCostSummary smcs
            WHERE smcs.site.id = :siteId
            AND smcs.siteProcess.id = :siteProcessId
            AND smcs.deleted = false
            ORDER BY smcs.yearMonth""")
    List<Object[]> findMonthlyCostsBySiteIdAndSiteProcessId(
            @Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId);
}

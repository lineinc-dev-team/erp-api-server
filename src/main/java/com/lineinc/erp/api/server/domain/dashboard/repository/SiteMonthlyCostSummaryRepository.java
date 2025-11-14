package com.lineinc.erp.api.server.domain.dashboard.repository;

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
}

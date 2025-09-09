package com.lineinc.erp.api.server.domain.laborpayroll.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

/**
 * 노무명세서 집계 Repository
 */
@Repository
public interface LaborPayrollSummaryRepository extends JpaRepository<LaborPayrollSummary, Long> {

    /**
     * 현장, 공정, 년월로 집계 데이터 조회
     */
    Optional<LaborPayrollSummary> findBySiteAndSiteProcessAndYearMonth(
            Site site, SiteProcess siteProcess, String yearMonth);

    /**
     * 조건에 따른 집계 데이터 페이징 조회
     */
    @Query("""
            SELECT lps FROM LaborPayrollSummary lps
            JOIN FETCH lps.site s
            JOIN FETCH lps.siteProcess sp
            WHERE (:siteId IS NULL OR lps.site.id = :siteId)
            AND (:siteProcessId IS NULL OR lps.siteProcess.id = :siteProcessId)
            AND (:yearMonth IS NULL OR lps.yearMonth = :yearMonth)
            """)
    Page<LaborPayrollSummary> findBySearchCondition(
            @Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId,
            @Param("yearMonth") String yearMonth,
            Pageable pageable);

    /**
     * 특정 년월의 모든 집계 데이터 삭제
     */
    void deleteByYearMonth(String yearMonth);

    /**
     * 현장, 공정, 년월의 집계 데이터 삭제
     */
    void deleteBySiteAndSiteProcessAndYearMonth(Site site, SiteProcess siteProcess, String yearMonth);
}

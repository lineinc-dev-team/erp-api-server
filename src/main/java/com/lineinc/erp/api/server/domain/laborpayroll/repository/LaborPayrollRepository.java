package com.lineinc.erp.api.server.domain.laborpayroll.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

/**
 * 노무비 명세서 Repository
 */
@Repository
public interface LaborPayrollRepository extends JpaRepository<LaborPayroll, Long> {

    /**
     * 특정 인력과 년월의 노무비 명세서 조회
     */
    Optional<LaborPayroll> findByLaborIdAndYearMonth(Long laborId, String yearMonth);

    /**
     * 특정 년월의 모든 노무비 명세서 조회
     */
    List<LaborPayroll> findByYearMonth(String yearMonth);

    /**
     * 특정 인력의 노무비 명세서 조회 (년월 범위)
     */
    @Query("""
            SELECT lp FROM LaborPayroll lp
            WHERE lp.labor.id = :laborId
            AND lp.yearMonth BETWEEN :startYearMonth AND :endYearMonth
            ORDER BY lp.yearMonth
            """)
    List<LaborPayroll> findByLaborIdAndYearMonthBetween(@Param("laborId") Long laborId,
            @Param("startYearMonth") String startYearMonth,
            @Param("endYearMonth") String endYearMonth);

    /**
     * 특정 인력의 특정 년월 데이터 삭제
     */
    void deleteByLaborIdAndYearMonth(Long laborId, String yearMonth);

    /**
     * 현장/공정/년월 조건으로 노무명세서 조회
     */
    @Query("""
            SELECT lp FROM LaborPayroll lp
            JOIN FETCH lp.labor
            JOIN FETCH lp.site s
            JOIN FETCH lp.siteProcess sp
            WHERE (:siteId IS NULL OR lp.site.id = :siteId)
            AND (:siteProcessId IS NULL OR lp.siteProcess.id = :siteProcessId)
            AND (:yearMonth IS NULL OR lp.yearMonth = :yearMonth)
            ORDER BY lp.yearMonth, s.name, sp.name, lp.labor.name
            """)
    List<LaborPayroll> findBySearchCondition(
            @Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId,
            @Param("yearMonth") String yearMonth);

    /**
     * 현장, 공정, 년월로 노무명세서 조회
     */
    List<LaborPayroll> findBySiteAndSiteProcessAndYearMonth(Site site, SiteProcess siteProcess, String yearMonth);
}

package com.lineinc.erp.api.server.domain.dailyreport.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingConstructionGroup;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

@Repository
public interface DailyReportOutsourcingConstructionGroupRepository
        extends JpaRepository<DailyReportOutsourcingConstructionGroup, Long> {

    /**
     * 현장, 공정, 일자로 외주(공사) 그룹을 슬라이스로 조회합니다.
     * 
     * @param site        현장
     * @param siteProcess 공정
     * @param reportDate  출역일보 일자
     * @param pageable    페이징 정보
     * @return 외주(공사) 그룹 슬라이스
     */
    @Query("""
            SELECT g FROM DailyReportOutsourcingConstructionGroup g
            JOIN g.dailyReport dr
            WHERE dr.site = :site
            AND dr.siteProcess = :siteProcess
            AND dr.reportDate = :reportDate
            AND g.deleted = false
            AND dr.deleted = false
            ORDER BY g.createdAt DESC
            """)
    Slice<DailyReportOutsourcingConstructionGroup> findBySiteAndSiteProcessAndReportDate(
            @Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess,
            @Param("reportDate") OffsetDateTime reportDate,
            Pageable pageable);

    /**
     * 현장, 공정, 월 구간(포함/미만)으로 외주(공사) 그룹 조회 (집계용)
     * 외주업체를 JOIN FETCH하여 N+1 문제 방지
     * - startDate: 조회월 1일 00:00 UTC 이상
     * - endDate : 다음달 1일 00:00 UTC 미만
     */
    @Query("""
            SELECT DISTINCT g FROM DailyReportOutsourcingConstructionGroup g
            JOIN FETCH g.dailyReport dr
            LEFT JOIN FETCH g.outsourcingCompany oc
            WHERE dr.site.id = :siteId
            AND dr.siteProcess.id = :siteProcessId
            AND dr.reportDate >= :startDate
            AND dr.reportDate < :endDate
            AND g.deleted = false
            AND dr.deleted = false
            AND g.outsourcingCompany IS NOT NULL
            """)
    List<DailyReportOutsourcingConstructionGroup> findBySiteAndSiteProcessAndReportDateBetweenMonth(
            @Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    /**
     * 현장, 공정으로 외주(공사) 그룹 조회 (집계용)
     * 외주업체, 외주업체계약 공사항목 그룹, 외주업체계약을 JOIN FETCH하여 N+1 문제 방지
     */
    @Query("""
            SELECT DISTINCT g FROM DailyReportOutsourcingConstructionGroup g
            JOIN FETCH g.dailyReport dr
            LEFT JOIN FETCH g.outsourcingCompany oc
            LEFT JOIN FETCH g.outsourcingCompanyContractConstructionGroup cg
            LEFT JOIN FETCH cg.outsourcingCompanyContract contract
            WHERE dr.site.id = :siteId
            AND dr.siteProcess.id = :siteProcessId
            AND g.deleted = false
            AND dr.deleted = false
            AND g.outsourcingCompany IS NOT NULL
            AND g.outsourcingCompanyContractConstructionGroup IS NOT NULL
            """)
    List<DailyReportOutsourcingConstructionGroup> findBySiteAndSiteProcess(
            @Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId);
}

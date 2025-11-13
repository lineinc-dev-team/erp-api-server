package com.lineinc.erp.api.server.domain.fuelaggregation.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

@Repository
public interface FuelInfoRepository extends JpaRepository<FuelInfo, Long> {

    /**
     * 특정 현장, 공정, 일자의 FuelInfo를 페이징으로 조회합니다.
     * DailyReportFuel -> FuelAggregation -> FuelInfo 경로로 조인합니다.
     */
    @Query("""
            SELECT fi FROM FuelInfo fi
            JOIN fi.fuelAggregation fa
            JOIN DailyReportFuel drf ON drf.fuelAggregation = fa
            JOIN drf.dailyReport dr
            WHERE dr.site = :site
            AND dr.siteProcess = :siteProcess
            AND dr.reportDate = :reportDate
            AND fa.deleted = false
            AND fi.deleted = false
            ORDER BY fi.id
            """)
    Slice<FuelInfo> findByDailyReportSiteAndProcessAndDate(
            @Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess,
            @Param("reportDate") OffsetDateTime reportDate,
            Pageable pageable);

    /**
     * 특정 현장, 공정의 출역일보 유류 정보를 조회 종료일 이전까지 조회합니다.
     */
    @Query("""
            SELECT DISTINCT fi AS fuelInfo, dr.reportDate AS reportDate
            FROM FuelInfo fi
            JOIN fi.fuelAggregation fa
            JOIN DailyReportFuel drf ON drf.fuelAggregation = fa
            JOIN drf.dailyReport dr
            WHERE dr.site.id = :siteId
            AND dr.siteProcess.id = :siteProcessId
            AND dr.reportDate < :endDate
            AND fa.deleted = false
            AND fi.deleted = false
            """)
    List<FuelInfoWithReportDate> findBySiteIdAndSiteProcessIdAndReportDateLessThan(
            @Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId,
            @Param("endDate") OffsetDateTime endDate);
}

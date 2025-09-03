package com.lineinc.erp.api.server.domain.dailyreport.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.WeatherType;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    /**
     * 현장, 공정, 일자, 날씨(선택사항)로 출역일보를 슬라이스로 조회합니다.
     * 날씨가 null이면 날씨 조건 없이 조회합니다.
     * 
     * @param site        현장
     * @param siteProcess 공정
     * @param reportDate  출역일보 일자
     * @param weather     날씨 (null이면 전체 조회)
     * @param pageable    페이징 정보
     * @return 출역일보 슬라이스
     */
    @Query("SELECT dr FROM DailyReport dr WHERE dr.site = :site AND dr.siteProcess = :siteProcess " +
            "AND dr.reportDate = :reportDate " +
            "AND (:weather IS NULL OR dr.weather = :weather)")
    Slice<DailyReport> findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
            @Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess,
            @Param("reportDate") OffsetDateTime reportDate,
            @Param("weather") WeatherType weather,
            Pageable pageable);

    /**
     * 특정 인력의 특정 기간 출역일보를 조회합니다.
     * 직영/계약직 인력만 고려합니다.
     */
    @Query("SELECT DISTINCT dr FROM DailyReport dr " +
            "LEFT JOIN dr.employees e " +
            "LEFT JOIN dr.directContracts dc " +
            "WHERE (e.labor.id = :laborId OR dc.labor.id = :laborId) " +
            "AND dr.reportDate >= :startDate AND dr.reportDate <= :endDate")
    List<DailyReport> findByLaborIdAndDateRange(@Param("laborId") Long laborId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    /**
     * 같은 날짜, 현장, 공정에 대한 출역일보 존재 여부를 확인합니다.
     */
    @Query("SELECT COUNT(dr) > 0 FROM DailyReport dr " +
            "WHERE dr.site.id = :siteId " +
            "AND dr.siteProcess.id = :siteProcessId " +
            "AND dr.reportDate = :reportDate")
    boolean existsBySiteAndSiteProcessAndReportDate(@Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId,
            @Param("reportDate") OffsetDateTime reportDate);

    /**
     * 같은 날짜, 현장, 공정에 대한 출역일보를 조회합니다.
     */
    @Query("SELECT dr FROM DailyReport dr " +
            "WHERE dr.site = :site " +
            "AND dr.siteProcess = :siteProcess " +
            "AND dr.reportDate = :reportDate")
    Optional<DailyReport> findBySiteAndSiteProcessAndReportDate(@Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess,
            @Param("reportDate") OffsetDateTime reportDate);

    /**
     * 특정 인력의 지정된 날짜 이후 출근 기록이 있는지 확인합니다.
     * 계약직 인력만 대상으로 합니다.
     */
    @Query("SELECT COUNT(dr) > 0 FROM DailyReport dr " +
            "LEFT JOIN dr.directContracts dc " +
            "WHERE dc.labor.id = :laborId " +
            "AND dr.reportDate >= :startDate")
    boolean hasWorkRecordSince(@Param("laborId") Long laborId, @Param("startDate") OffsetDateTime startDate);

    /**
     * 특정 인력의 지난달 근로시간(공수)을 계산합니다.
     * 계약직 인력만 대상으로 합니다.
     */
    @Query("SELECT COALESCE(SUM(dc.workQuantity), 0) " +
            "FROM DailyReport dr " +
            "LEFT JOIN dr.directContracts dc " +
            "WHERE dc.labor.id = :laborId " +
            "AND dr.reportDate >= :startDate AND dr.reportDate < :endDate")
    Double calculateLastMonthWorkHours(@Param("laborId") Long laborId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);
}

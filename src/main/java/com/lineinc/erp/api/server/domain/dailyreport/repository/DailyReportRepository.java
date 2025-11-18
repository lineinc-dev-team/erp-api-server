package com.lineinc.erp.api.server.domain.dailyreport.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long>, DailyReportRepositoryCustom {

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
    @Query("""
            SELECT dr FROM DailyReport dr WHERE dr.site = :site AND dr.siteProcess = :siteProcess \
            AND dr.reportDate = :reportDate \
            AND (:weather IS NULL OR dr.weather = :weather) \
            AND dr.deleted = false""")
    Slice<DailyReport> findBySiteAndSiteProcessAndReportDateAndWeatherOptional(
            @Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess,
            @Param("reportDate") OffsetDateTime reportDate,
            @Param("weather") FuelAggregationWeatherType weather,
            Pageable pageable);

    /**
     * 같은 날짜, 현장, 공정에 대한 출역일보 존재 여부를 확인합니다.
     */
    @Query("""
            SELECT COUNT(dr) > 0 FROM DailyReport dr \
            WHERE dr.site.id = :siteId \
            AND dr.siteProcess.id = :siteProcessId \
            AND dr.reportDate = :reportDate \
            AND dr.deleted = false""")
    boolean existsBySiteAndSiteProcessAndReportDate(@Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId,
            @Param("reportDate") OffsetDateTime reportDate);

    /**
     * 같은 날짜, 현장, 공정에 대한 출역일보를 조회합니다.
     */
    @Query("""
            SELECT dr FROM DailyReport dr \
            WHERE dr.site = :site \
            AND dr.siteProcess = :siteProcess \
            AND dr.reportDate = :reportDate \
            AND dr.deleted = false""")
    Optional<DailyReport> findBySiteAndSiteProcessAndReportDate(@Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess,
            @Param("reportDate") OffsetDateTime reportDate);

    /**
     * 특정 날짜 이전의 특정 상태 출역일보를 조회합니다.
     * 자동 마감 배치에서 사용됩니다.
     */
    @Query("""
            SELECT dr FROM DailyReport dr \
            WHERE dr.reportDate < :beforeDate \
            AND dr.status = :status \
            AND dr.deleted = false""")
    List<DailyReport> findByReportDateBeforeAndStatus(@Param("beforeDate") OffsetDateTime beforeDate,
            @Param("status") DailyReportStatus status);

    /**
     * 특정 현장, 공정, 날짜 범위로 출역일보를 조회합니다.
     * 노무비 명세서 동기화에서 사용됩니다.
     * 마감 상태(COMPLETED, AUTO_COMPLETED)인 출역일보만 조회합니다.
     */
    @Query("""
            SELECT dr FROM DailyReport dr \
            WHERE dr.site = :site \
            AND dr.siteProcess = :siteProcess \
            AND dr.reportDate >= :startDate \
            AND dr.reportDate <= :endDate \
            AND dr.status IN :statuses \
            AND dr.deleted = false \
            ORDER BY dr.reportDate ASC""")
    List<DailyReport> findBySiteAndSiteProcessAndReportDateBetween(@Param("site") Site site,
            @Param("siteProcess") SiteProcess siteProcess,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("statuses") List<DailyReportStatus> statuses);

    /**
     * 특정 현장, 공정, 월 구간(포함/미만)으로 출역일보 조회 (날씨 수집용)
     * 마감 상태(COMPLETED, AUTO_COMPLETED)인 출역일보만 조회합니다.
     */
    @Query("""
            SELECT dr FROM DailyReport dr \
            WHERE dr.site.id = :siteId \
            AND dr.siteProcess.id = :siteProcessId \
            AND dr.reportDate >= :startDate \
            AND dr.reportDate < :endDate \
            AND dr.status IN :statuses \
            AND dr.deleted = false \
            ORDER BY dr.reportDate ASC""")
    List<DailyReport> findBySiteIdAndSiteProcessIdAndReportDateBetweenMonth(
            @Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("statuses") List<DailyReportStatus> statuses);

}

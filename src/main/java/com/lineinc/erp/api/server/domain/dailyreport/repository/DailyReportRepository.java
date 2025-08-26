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
}

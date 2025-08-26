package com.lineinc.erp.api.server.domain.dailyreport.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.WeatherType;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    /**
     * 현장, 공정, 일자, 날씨로 출역일보를 슬라이스로 조회합니다.
     * 
     * @param site        현장
     * @param siteProcess 공정
     * @param reportDate  출역일보 일자
     * @param weather     날씨
     * @param pageable    페이징 정보
     * @return 출역일보 슬라이스
     */
    Slice<DailyReport> findBySiteAndSiteProcessAndReportDateAndWeather(
            Site site, SiteProcess siteProcess, OffsetDateTime reportDate, WeatherType weather, Pageable pageable);
}

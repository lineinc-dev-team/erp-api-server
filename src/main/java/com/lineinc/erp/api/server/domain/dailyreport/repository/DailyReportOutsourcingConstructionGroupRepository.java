package com.lineinc.erp.api.server.domain.dailyreport.repository;

import java.time.OffsetDateTime;

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
}

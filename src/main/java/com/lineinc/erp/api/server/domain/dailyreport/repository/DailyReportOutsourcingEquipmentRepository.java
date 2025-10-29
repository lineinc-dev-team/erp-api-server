package com.lineinc.erp.api.server.domain.dailyreport.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportOutsourcingEquipment;

/**
 * 출역일보 외주업체 장비 Repository
 */
@Repository
public interface DailyReportOutsourcingEquipmentRepository
        extends JpaRepository<DailyReportOutsourcingEquipment, Long> {

    /**
     * 현장, 공정, 날짜 범위로 외주업체 장비 조회 (집계용)
     * 외주업체, 서브장비를 JOIN FETCH하여 N+1 문제 방지
     */
    @Query("""
            SELECT DISTINCT droe FROM DailyReportOutsourcingEquipment droe
            JOIN FETCH droe.dailyReport dr
            LEFT JOIN FETCH droe.outsourcingCompany oc
            LEFT JOIN FETCH droe.outsourcingCompanyContractDriver driver
            LEFT JOIN FETCH droe.subEquipments se
            WHERE dr.site.id = :siteId
            AND dr.siteProcess.id = :siteProcessId
            AND dr.reportDate <= :endDate
            AND droe.outsourcingCompany IS NOT NULL
            """)
    List<DailyReportOutsourcingEquipment> findBySiteAndSiteProcessAndReportDateLessThanEqual(
            @Param("siteId") Long siteId,
            @Param("siteProcessId") Long siteProcessId,
            @Param("endDate") OffsetDateTime endDate);
}

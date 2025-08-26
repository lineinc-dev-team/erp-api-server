package com.lineinc.erp.api.server.domain.dailyreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
}

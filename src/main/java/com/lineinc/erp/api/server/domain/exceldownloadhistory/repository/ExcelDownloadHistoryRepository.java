package com.lineinc.erp.api.server.domain.exceldownloadhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lineinc.erp.api.server.domain.exceldownloadhistory.entity.ExcelDownloadHistory;

/**
 * 엑셀 다운로드 이력 Repository
 */
public interface ExcelDownloadHistoryRepository extends JpaRepository<ExcelDownloadHistory, Long> {
}

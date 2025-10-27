package com.lineinc.erp.api.server.domain.exceldownloadhistory.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 엑셀 다운로드 타입
 */
@Getter
@RequiredArgsConstructor
public enum ExcelDownloadHistoryType {
    CLIENT_COMPANY("발주처 목록"),
    SITE("현장 목록"),
    MATERIAL_MANAGEMENT("자재관리 목록"),
    STEEL_MANAGEMENT("강재수불부 목록"),
    STEEL_MANAGEMENT_DETAIL("강재수불부 상세 목록"),
    FUEL_AGGREGATION("유류집계 목록"),
    LABOR_MANAGEMENT("인력정보 목록"),
    LABOR_PAYROLL("노무명세서 목록"),
    OUTSOURCING_COMPANY("외주업체 목록"),
    OUTSOURCING_COMPANY_CONTRACT("외주업체 계약 목록"),
    MANAGEMENT_COST("관리비 목록"),
    SITE_MANAGEMENT_COST("현장관리비 목록"),
    ACCOUNT("유저 목록");

    private final String label;
}

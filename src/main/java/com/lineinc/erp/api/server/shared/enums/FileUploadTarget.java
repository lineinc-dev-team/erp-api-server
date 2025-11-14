package com.lineinc.erp.api.server.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 엑셀 파일 업로드 타입
 */
@Getter
@RequiredArgsConstructor
public enum FileUploadTarget {
    CLIENT_COMPANY("client-company", "발주처"),
    OUTSOURCING_COMPANY("outsourcing-company", "외주업체"),
    OUTSOURCING_COMPANY_CONTRACT("outsourcing-company-contract", "외주업체 계약"),
    SITE("site", "현장"),
    MANAGEMENT_COST("management-cost", "관리비"),
    SITE_MANAGEMENT_COST("site-management-cost", "현장/본사 관리비"),
    STEEL_MANAGEMENT("steel-management", "강재수불부"),
    MATERIAL_MANAGEMENT("material-management", "자재 관리"),
    FUEL_AGGREGATION("fuel-aggregation", "유류집계"),
    LABOR_MANAGEMENT("labor-management", "노무 관리"),
    WORK_DAILY_REPORT("work-daily-report", "출역일보"),

    // 집계 관련
    AGGREGATION_TABLE("aggregation-table", "집계표(본사)"),
    AGGREGATION_MATERIAL_COST("aggregation-material-cost", "집계표 재료비"),
    AGGREGATION_FUEL("aggregation-fuel", "집계표 유류집계"),
    AGGREGATION_LABOR_COST("aggregation-labor-cost", "집계표 노무비"),
    AGGREGATION_LABOR_PAYROLL("aggregation-labor-payroll", "집계표 노무비명세서"),
    AGGREGATION_EQUIPMENT_COST("aggregation-equipment-cost", "집계표 장비비"),
    AGGREGATION_EQUIPMENT_OPERATION("aggregation-equipment-operation", "집계표 장비가동현황"),
    AGGREGATION_MANAGEMENT_COST("aggregation-management-cost", "집계표 관리비"),
    AGGREGATION_OUTSOURCING_CONSTRUCTION("aggregation-outsourcing-construction", "집계표 외주");

    private final String directory;
    private final String label;
}

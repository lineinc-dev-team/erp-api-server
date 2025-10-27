package com.lineinc.erp.api.server.shared.enums;

import lombok.Getter;

@Getter
public enum FileUploadTarget {
    CLIENT_COMPANY("client-company"),
    OUTSOURCING_COMPANY("outsourcing-company"),
    OUTSOURCING_COMPANY_CONTRACT("outsourcing-company-contract"),
    SITE("site"),
    MANAGEMENT_COST("management-cost"),
    SITE_MANAGEMENT_COST("site-management-cost"),
    STEEL_MANAGEMENT("steel-management"),
    MATERIAL_MANAGEMENT("material-management"),
    FUEL_AGGREGATION("fuel-aggregation"),
    LABOR_MANAGEMENT("labor-management"),
    WORK_DAILY_REPORT("work-daily-report"),

    // 집계 관련
    AGGREGATION_TABLE("aggregation-table"),
    AGGREGATION_MATERIAL_COST("aggregation-material-cost"),
    AGGREGATION_FUEL("aggregation-fuel"),
    AGGREGATION_LABOR_COST("aggregation-labor-cost"),
    AGGREGATION_LABOR_PAYROLL("aggregation-labor-payroll"),
    AGGREGATION_EQUIPMENT_COST("aggregation-equipment-cost"),
    AGGREGATION_EQUIPMENT_OPERATION("aggregation-equipment-operation"),
    AGGREGATION_MANAGEMENT_COST("aggregation-management-cost"),
    AGGREGATION_ADVANCE_PAYMENT("aggregation-advance-payment"),
    AGGREGATION_OUTSOURCING_CONSTRUCTION("aggregation-outsourcing-construction");

    private final String directory;

    FileUploadTarget(final String directory) {
        this.directory = directory;
    }
}

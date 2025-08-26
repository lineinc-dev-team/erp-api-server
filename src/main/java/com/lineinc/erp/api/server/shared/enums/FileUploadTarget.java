package com.lineinc.erp.api.server.shared.enums;

import lombok.Getter;

@Getter
public enum FileUploadTarget {
    CLIENT_COMPANY("client-company"),
    OUTSOURCING_COMPANY("outsourcing-company"),
    OUTSOURCING_COMPANY_CONTRACT("outsourcing-company-contract"),
    SITE("site"),
    MANAGEMENT_COST("management-cost"),
    STEEL_MANAGEMENT("steel-management"),
    MATERIAL_MANAGEMENT("material-management"),
    FUEL_AGGREGATION("fuel-aggregation"),
    LABOR_MANAGEMENT("labor-management"),
    WORK_DAILY_REPORT("work-daily-report");

    private final String directory;

    FileUploadTarget(String directory) {
        this.directory = directory;
    }
}

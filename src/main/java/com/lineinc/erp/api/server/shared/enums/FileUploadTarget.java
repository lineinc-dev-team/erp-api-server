package com.lineinc.erp.api.server.shared.enums;

import lombok.Getter;

@Getter
public enum FileUploadTarget {
    CLIENT_COMPANY("client-company"),
    OUTSOURCING_COMPANY("outsourcing-company"),
    SITE("site"),
    MANAGEMENT_COST("management-cost"),
    STEEL_MANAGEMENT("steel-management"),
    MATERIAL_MANAGEMENT("material-management");

    private final String directory;

    FileUploadTarget(String directory) {
        this.directory = directory;
    }

}

package com.lineinc.erp.api.server.common.enums;

import lombok.Getter;

@Getter
public enum FileUploadTarget {
    CLIENT_COMPANY("client-company"),
    SITE("site"),
    MANAGEMENT_COST("management-cost");

    private final String directory;

    FileUploadTarget(String directory) {
        this.directory = directory;
    }

}

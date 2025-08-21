package com.lineinc.erp.api.server.domain.labormanagement.enums;

public enum LaborChangeType {
    BASIC("정보"),
    ATTACHMENT("첨부파일");

    private final String label;

    LaborChangeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

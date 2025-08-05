package com.lineinc.erp.api.server.domain.site.enums;

import lombok.Getter;

@Getter
public enum SiteChangeType {
    BASIC("현장정보"),
    PROCESS("공정정보"),
    CONTRACT("계약서 관리");

    private final String label;

    SiteChangeType(String label) {
        this.label = label;
    }

}

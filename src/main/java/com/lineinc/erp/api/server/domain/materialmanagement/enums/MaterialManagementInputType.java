package com.lineinc.erp.api.server.domain.materialmanagement.enums;

import lombok.Getter;

@Getter
public enum MaterialManagementInputType {
    MAJOR_PURCHASE("주요자재(구매)"),
    MAJOR_LEASE("주요자재(임대)"),
    MAJOR_INTERNAL("주요자재(자사)"),
    CIVIL_SUPPORT("부대토목자재"),
    TOOL_MISC("잡자재(공구)"),
    METAL_MISC("잡자재(잡철)"),
    SAFETY("안전(안전관리비)"),
    ENVIRONMENT("환경(환경관리비)"),
    TRANSPORT("운반비");

    private final String label;

    MaterialManagementInputType(String label) {
        this.label = label;
    }
}
package com.lineinc.erp.api.server.domain.materialmanagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaterialManagementInputType {
    DIRECT_INPUT("직접입력", 1),
    MAJOR_PURCHASE("주요자재(구매)", 2),
    MAJOR_LEASE("주요자재(임대)", 3),
    MAJOR_INTERNAL("주요자재(자사)", 4),
    CIVIL_SUPPORT("부대토목자재", 5),
    TOOL_MISC("잡자재(공구)", 6),
    METAL_MISC("잡자재(잡철)", 7),
    SAFETY("안전(안전관리비)", 8),
    ENVIRONMENT("환경(환경관리비)", 9),
    TRANSPORT("운반비", 10);

    private final String label;
    private final int order;
}
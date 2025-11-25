package com.lineinc.erp.api.server.domain.materialmanagement.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaterialManagementInputType {
    DIRECT_INPUT("직접입력", 0),
    CIVIL_SUPPORT("부대토목자재", 1),
    TOOL_MISC("잡자재(공구)", 2),
    METAL_MISC("잡자재(잡철)", 3),
    SAFETY("안전(안전관리비)", 4),
    ENVIRONMENT("환경(환경관리비)", 5),
    TRANSPORT("운반비", 6);

    private final String label;
    private final int order;
}

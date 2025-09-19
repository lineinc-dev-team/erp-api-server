package com.lineinc.erp.api.server.domain.site.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SiteChangeHistoryType {
    BASIC("현장정보"),
    PROCESS("공정정보"),
    CONTRACT("계약서 관리");

    private final String label;
}

package com.lineinc.erp.api.server.domain.steelmanagementv2.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 강재수불부 V2 상세 타입
 */
@Getter
@RequiredArgsConstructor
public enum SteelManagementDetailV2Type {
    INCOMING("입고"),
    OUTGOING("출고"),
    ON_SITE_STOCK("사장"),
    SCRAP("고철");

    private final String label;
}

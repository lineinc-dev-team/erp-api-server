package com.lineinc.erp.api.server.domain.steelmanagementv2.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 강재수불부 V2 상세 구분
 */
@Getter
@RequiredArgsConstructor
public enum SteelManagementDetailV2Category {
    OWN_MATERIAL("자사자재"),
    PURCHASE("구매"),
    RENTAL("임대");

    private final String label;
}

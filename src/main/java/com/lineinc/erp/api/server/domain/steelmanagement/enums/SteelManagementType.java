package com.lineinc.erp.api.server.domain.steelmanagement.enums;

import lombok.Getter;

@Getter
public enum SteelManagementType {
    ORDER("발주"),
    APPROVAL("승인"),
    RELEASE("반출"),
    PURCHASE("매입"),
    LEASE("임대");

    private final String label;

    SteelManagementType(final String label) {
        this.label = label;
    }

}

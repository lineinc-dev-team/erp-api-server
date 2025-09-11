package com.lineinc.erp.api.server.domain.client.enums;

import lombok.Getter;

@Getter
public enum ClientCompanyPaymentMethod {
    CASH("현금"),
    BILL("어음");

    private final String displayName;

    ClientCompanyPaymentMethod(String displayName) {
        this.displayName = displayName;
    }
}
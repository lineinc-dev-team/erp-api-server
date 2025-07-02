package com.lineinc.erp.api.server.domain.client;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CASH("현금"),
    BILL("어음");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
}
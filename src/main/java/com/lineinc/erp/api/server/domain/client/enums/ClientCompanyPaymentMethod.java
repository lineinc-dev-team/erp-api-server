package com.lineinc.erp.api.server.domain.client.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientCompanyPaymentMethod {
    CASH("현금"),
    BILL("어음");

    private final String label;
}

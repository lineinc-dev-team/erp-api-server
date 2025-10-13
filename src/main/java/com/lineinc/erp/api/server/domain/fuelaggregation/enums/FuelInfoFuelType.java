package com.lineinc.erp.api.server.domain.fuelaggregation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FuelInfoFuelType {
    GASOLINE("휘발유"),
    DIESEL("경유"),
    UREA("요소수"),
    ETC("기타");

    private final String label;
}

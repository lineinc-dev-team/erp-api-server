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

    public static FuelInfoFuelType fromLabel(final String label) {
        return switch (label) {
            case "휘발유" -> GASOLINE;
            case "경유" -> DIESEL;
            case "요소수" -> UREA;
            case "기타" -> ETC;
            default -> throw new IllegalArgumentException("지원하지 않는 유종입니다: " + label);
        };
    }
}

package com.lineinc.erp.api.server.domain.fuelaggregation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FuelAggregationChangeType {
    BASIC("기본정보"),
    FUEL_INFO("유류정보");

    private final String label;
}

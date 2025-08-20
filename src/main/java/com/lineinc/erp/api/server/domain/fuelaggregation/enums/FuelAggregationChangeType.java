package com.lineinc.erp.api.server.domain.fuelaggregation.enums;

import lombok.Getter;

@Getter
public enum FuelAggregationChangeType {
    BASIC("기본정보"),
    FUEL_INFO("유류정보");

    private final String label;

    FuelAggregationChangeType(String label) {
        this.label = label;
    }
}

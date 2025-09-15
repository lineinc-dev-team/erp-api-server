package com.lineinc.erp.api.server.domain.fuelaggregation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FuelAggregationWeatherType {
    SUNNY("맑음"),
    CLOUDY("흐림"),
    RAINY("비");

    private final String label;
}

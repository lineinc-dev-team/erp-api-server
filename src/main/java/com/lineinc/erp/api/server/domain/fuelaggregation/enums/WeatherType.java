package com.lineinc.erp.api.server.domain.fuelaggregation.enums;

import lombok.Getter;

@Getter
public enum WeatherType {
    SUNNY("맑음"),
    CLOUDY("흐림"),
    RAINY("비");

    private final String label;

    WeatherType(String label) {
        this.label = label;
    }
}

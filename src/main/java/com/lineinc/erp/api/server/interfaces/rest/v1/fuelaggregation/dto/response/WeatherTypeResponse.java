package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import com.lineinc.erp.api.server.shared.enums.WeatherType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "날씨 타입 응답")
public record WeatherTypeResponse(
        @Schema(description = "날씨 타입 코드", example = "SUNNY") String code,
        @Schema(description = "날씨 타입 라벨", example = "맑음") String name) {

    public static WeatherTypeResponse from(WeatherType weatherType) {
        return new WeatherTypeResponse(weatherType.name(), weatherType.getLabel());
    }
}

package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유종 타입 응답")
public record FuelTypeResponse(
        @Schema(description = "유종 타입 코드", example = "DIESEL") String code,
        @Schema(description = "유종 타입 이름", example = "경유") String name) {

    public static FuelTypeResponse from(FuelInfoFuelType fuelType) {
        return new FuelTypeResponse(fuelType.name(), fuelType.getLabel());
    }
}

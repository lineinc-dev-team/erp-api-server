package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "유류정보 추가 요청")
public record AddFuelInfoRequest(
        @NotNull @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,

        @NotNull @Schema(description = "기사 ID", example = "1") Long driverId,

        @NotNull @Schema(description = "장비 ID", example = "1") Long equipmentId,

        @NotNull @Schema(description = "유종", example = "DIESEL") FuelType fuelType,

        @NotNull @Positive @Schema(description = "주유량 (리터)", example = "50") Long fuelAmount,

        @Schema(description = "비고", example = "오전 주유") String memo) {
}

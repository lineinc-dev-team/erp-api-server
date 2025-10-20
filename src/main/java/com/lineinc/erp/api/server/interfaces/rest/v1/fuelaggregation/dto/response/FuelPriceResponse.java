package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류집계 유종별 가격 정보 응답")
public record FuelPriceResponse(
        @Schema(description = "유류집계 ID", example = "1") Long id,
        @Schema(description = "휘발유 가격 (원)", example = "150000") Long gasolinePrice,
        @Schema(description = "경유 가격 (원)", example = "200000") Long dieselPrice,
        @Schema(description = "요소수 가격 (원)", example = "50000") Long ureaPrice) {
}

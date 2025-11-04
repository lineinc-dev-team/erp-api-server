package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유류집계 생성 요청")
public record FuelAggregationCreateRequest(
        @Schema(description = "형장 ID", example = "1") @NotNull Long siteId,
        @Schema(description = "공정 ID", example = "1") @NotNull Long siteProcessId,
        @Schema(description = "일자", example = "2025-01-15") @NotNull LocalDate date,
        @Schema(description = "날씨", example = "SUNNY") @NotNull FuelAggregationWeatherType weather,
        @Schema(description = "유류업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "휘발유 가격 (원)", example = "150000") Long gasolinePrice,
        @Schema(description = "경유 가격 (원)", example = "200000") Long dieselPrice,
        @Schema(description = "요소수 가격 (원)", example = "50000") Long ureaPrice,
        @Schema(description = "유류정보 목록") List<FuelInfoCreateRequest> fuelInfos) {
}

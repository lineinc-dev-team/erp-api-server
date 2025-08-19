package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류집계 응답")
public record FuelAggregationResponse(
        @Schema(description = "유류집계 ID", example = "1") Long id,
        @Schema(description = "현장명", example = "서울 APT 신축공사") String siteName,
        @Schema(description = "공정명", example = "기초공사") String processName,
        @Schema(description = "일자", example = "2025-01-15T00:00:00+09:00") OffsetDateTime date,
        @Schema(description = "날씨", example = "맑음") String weather,
        @Schema(description = "날씨 코드", example = "SUNNY") String weatherCode,
        @Schema(description = "유류정보 목록") List<FuelInfoResponse> fuelInfos) {

    public static FuelAggregationResponse from(FuelAggregation entity) {
        List<FuelInfoResponse> fuelInfoResponses = entity.getFuelInfos().stream()
                .map(FuelInfoResponse::from)
                .toList();

        return new FuelAggregationResponse(
                entity.getId(),
                entity.getSiteName(),
                entity.getProcessName(),
                entity.getDate(),
                entity.getWeather() != null ? entity.getWeather().getLabel() : null,
                entity.getWeather() != null ? entity.getWeather().name() : null,
                fuelInfoResponses);
    }
}

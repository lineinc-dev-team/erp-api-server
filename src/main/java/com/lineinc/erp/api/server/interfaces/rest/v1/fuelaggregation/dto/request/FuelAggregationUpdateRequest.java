package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelType;
import com.lineinc.erp.api.server.shared.enums.WeatherType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류집계 수정 요청")
public record FuelAggregationUpdateRequest(
        @Schema(description = "현장 ID", example = "1") Long siteId,

        @Schema(description = "공정 ID", example = "1") Long siteProcessId,

        @Schema(description = "일자", example = "2025-07-28") LocalDate date,

        @Schema(description = "날씨", example = "SUNNY") WeatherType weather,

        @Schema(description = "유류정보 수정 목록") List<FuelInfoUpdateRequest> fuelInfos,

        @Schema(description = "수정 이력 리스트") List<ChangeHistoryRequest> changeHistories) {

    @Schema(description = "유류정보 수정 요청")
    public record FuelInfoUpdateRequest(
            @Schema(description = "유류정보 ID (수정 시)", example = "1") Long id,

            @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,

            @Schema(description = "기사 ID", example = "1") Long driverId,

            @Schema(description = "장비 ID", example = "1") Long equipmentId,

            @Schema(description = "유종", example = "DIESEL") FuelType fuelType,

            @Schema(description = "주유량 (리터)", example = "50") Long fuelAmount,

            @Schema(description = "비고", example = "오전 주유") String memo) {
    }

    @Schema(description = "변경 이력 수정 요청")
    public record ChangeHistoryRequest(
            @Schema(description = "수정 이력 번호", example = "1") Long id,

            @Schema(description = "변경 사유 또는 비고", example = "변경에 따른 업데이트") String memo) {
    }
}

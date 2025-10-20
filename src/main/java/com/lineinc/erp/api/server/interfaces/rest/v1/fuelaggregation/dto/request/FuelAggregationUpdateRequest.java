package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import java.util.List;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유류집계 수정 요청")
public record FuelAggregationUpdateRequest(
        @Schema(description = "휘발유 가격 (원)", example = "150000") Long gasolinePrice,
        @Schema(description = "경유 가격 (원)", example = "200000") Long dieselPrice,
        @Schema(description = "요소수 가격 (원)", example = "50000") Long ureaPrice,
        @Schema(description = "유류정보 수정 목록") @Valid List<FuelInfoUpdateRequest> fuelInfos,
        @Schema(description = "수정 이력 리스트") @Valid List<ChangeHistoryRequest> changeHistories) {
    @Schema(description = "유류정보 수정 요청")
    public record FuelInfoUpdateRequest(
            @Schema(description = "유류정보 ID (수정 시)", example = "1") @NotNull Long id,
            @Schema(description = "외주업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
            @Schema(description = "기사 ID", example = "1") @NotNull Long driverId,
            @Schema(description = "장비 ID", example = "1") @NotNull Long equipmentId,
            @Schema(description = "유종", example = "DIESEL") @NotNull FuelInfoFuelType fuelType,
            @Schema(description = "주유량 (리터)", example = "50") @NotNull Long fuelAmount,
            @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
            @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
            @Schema(description = "비고", example = "오전 주유") String memo) {
    }

    @Schema(description = "변경 이력 수정 요청")
    public record ChangeHistoryRequest(
            @Schema(description = "수정 이력 번호", example = "1") Long id,
            @Schema(description = "변경 사유 또는 비고", example = "변경에 따른 업데이트") String memo) {
    }
}

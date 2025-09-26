package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유류정보 생성 요청")
public record FuelInfoCreateRequest(
        @Schema(description = "업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "기사 ID", example = "1") Long driverId,
        @Schema(description = "장비 ID", example = "1") Long equipmentId,
        @Schema(description = "유종", example = "GASOLINE") FuelInfoFuelType fuelType,
        @Schema(description = "주유량 (리터)", example = "50") Long fuelAmount,
        @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
        @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
        @Schema(description = "비고", example = "오전 주유") String memo) {
}

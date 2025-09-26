package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유류 출역일보 등록 요청")
public record DailyReportFuelCreateRequest(
        @Schema(description = "외주업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
        @Schema(description = "외주업체계약 기사 ID", example = "1") @NotNull Long driverId,
        @Schema(description = "외주업체계약 장비 ID", example = "1") @NotNull Long equipmentId,
        @Schema(description = "유종", example = "GASOLINE") @NotNull FuelInfoFuelType fuelType,
        @Schema(description = "주유량", example = "50") @NotNull Long fuelAmount,
        @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
        @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
        @Schema(description = "비고", example = "특별 지시사항") String memo) {
}

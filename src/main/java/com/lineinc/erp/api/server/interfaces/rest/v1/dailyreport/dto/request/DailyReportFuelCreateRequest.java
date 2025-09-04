package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유류 출역일보 등록 요청")
public record DailyReportFuelCreateRequest(
        @Schema(description = "외주업체계약 ID", example = "1") @NotNull Long outsourcingCompanyContractId,
        @Schema(description = "외주업체계약 기사 ID", example = "1") @NotNull Long outsourcingCompanyContractDriverId,
        @Schema(description = "외주업체계약 장비 ID", example = "1") @NotNull Long outsourcingCompanyContractEquipmentId,
        @Schema(description = "유종", example = "경유") @NotBlank String fuelType,
        @Schema(description = "주유량", example = "50") @NotNull Long fuelAmount,
        @Schema(description = "비고", example = "특별 지시사항") String memo) {
}

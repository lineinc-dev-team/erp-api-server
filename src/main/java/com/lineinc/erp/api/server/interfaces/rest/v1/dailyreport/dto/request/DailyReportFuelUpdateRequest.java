package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "출역일보 유류 수정 요청")
public record DailyReportFuelUpdateRequest(
        @Schema(description = "수정할 유류 정보 목록") List<@Valid FuelUpdateInfo> fuels) {

    @Schema(description = "유류 정보 수정 내용")
    public record FuelUpdateInfo(
            @Schema(description = "ID", example = "1") Long id,
            @Schema(description = "외주업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
            @Schema(description = "외주업체계약 기사 ID", example = "1") @NotNull Long outsourcingCompanyContractDriverId,
            @Schema(description = "외주업체계약 장비 ID", example = "1") @NotNull Long outsourcingCompanyContractEquipmentId,
            @Schema(description = "유종", example = "경유") @NotBlank String fuelType,
            @Schema(description = "주유량", example = "100") @NotNull Long fuelAmount,
            @Schema(description = "비고", example = "오전 주유") String memo) {
    }
}

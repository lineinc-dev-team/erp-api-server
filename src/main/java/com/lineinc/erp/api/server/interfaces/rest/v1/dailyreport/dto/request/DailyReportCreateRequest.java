package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.WeatherType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "출역일보 등록 요청")
public record DailyReportCreateRequest(
        @NotNull @Schema(description = "현장 ID", example = "1") Long siteId,

        @Schema(description = "공정 ID", example = "1") Long siteProcessId,

        @NotNull @Schema(description = "출역일보 일자", example = "2024-01-01") LocalDate reportDate,

        @Schema(description = "날씨", example = "SUNNY") WeatherType weather,

        @Schema(description = "직원 출역 정보") List<DailyReportEmployeeCreateRequest> employees,

        @Schema(description = "직영/계약직 출역 정보") List<DailyReportDirectContractCreateRequest> directContracts,

        @Schema(description = "외주 출역 정보") List<DailyReportOutsourcingCreateRequest> outsourcings,

        @Schema(description = "외주업체계약 장비 출역 정보") List<DailyReportOutsourcingEquipmentCreateRequest> outsourcingEquipments,

        @Schema(description = "유류 출역 정보") List<DailyReportFuelCreateRequest> fuels,

        @Schema(description = "현장 사진 정보") List<DailyReportFileCreateRequest> files) {
}

package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "출역일보 등록 요청")
public record DailyReportCreateRequest(
        @NotNull @Schema(description = "현장 ID", example = "1") Long siteId,
        @NotNull @Schema(description = "공정 ID", example = "1") Long siteProcessId,
        @NotNull @Schema(description = "출역일보 일자", example = "2024-01-01") LocalDate reportDate,
        @NotNull @Schema(description = "날씨", example = "SUNNY") FuelAggregationWeatherType weather,
        @Schema(description = "휘발유 가격 (원)", example = "150000") Long gasolinePrice,
        @Schema(description = "경유 가격 (원)", example = "200000") Long dieselPrice,
        @Schema(description = "요소수 가격 (원)", example = "50000") Long ureaPrice,
        @Schema(description = "비고", example = "특별 지시사항") String memo,
        @Schema(description = "직원 출역 정보") @Valid List<DailyReportEmployeeCreateRequest> employees,
        @Schema(description = "직영/계약직 출역 정보") @Valid List<DailyReportDirectContractCreateRequest> directContracts,
        @Schema(description = "외주 출역 정보") @Valid List<DailyReportOutsourcingCreateRequest> outsourcings,
        @Schema(description = "외주업체계약 장비 출역 정보") @Valid List<DailyReportOutsourcingEquipmentCreateRequest> outsourcingEquipments,
        @Schema(description = "유류 출역 정보") @Valid List<DailyReportFuelCreateRequest> fuelInfos,
        @Schema(description = "외주 공사 출역 정보") @Valid List<DailyReportOutsourcingConstructionCreateRequest> outsourcingConstructions,
        @Schema(description = "현장 사진 정보") @Valid List<DailyReportFileCreateRequest> files,
        @Schema(description = "증빙 파일 정보") @Valid List<DailyReportEvidenceFileCreateRequest> evidenceFiles,
        @Schema(description = "작업내용 정보") @Valid List<DailyReportWorkContentCreateRequest> workContents,
        @Schema(description = "주요공정 정보") @Valid List<DailyReportMainProcessCreateRequest> mainProcesses,
        @Schema(description = "투입현황 정보") @Valid List<DailyReportInputStatusCreateRequest> inputStatuses,
        @Schema(description = "자재현황 정보") @Valid List<DailyReportMaterialStatusCreateRequest> materialStatuses) {
}

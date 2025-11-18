package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse.SiteProcessSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse.SiteSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 목록 응답")
public record DailyReportListResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "현장 정보") SiteSimpleResponse site,
        @Schema(description = "공정 정보") SiteProcessSimpleResponse siteProcess,
        @Schema(description = "출역일보 일자", example = "2024-01-15T00:00:00+09:00") OffsetDateTime reportDate,
        @Schema(description = "날씨", example = "맑음") String weather,
        @Schema(description = "날씨 코드", example = "SUNNY") FuelAggregationWeatherType weatherCode,
        @Schema(description = "출역일보 상태", example = "PENDING") DailyReportStatus status,
        @Schema(description = "마감일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime completedAt,

        // 집계 데이터
        @Schema(description = "직원 공수합", example = "16.0") Double employeeWorkQuantitySum,
        @Schema(description = "직원 증빙 여부", example = "true") Boolean employeeEvidenceSubmitted,
        @Schema(description = "직영계약직 공수합", example = "8.0") Double directContractWorkQuantitySum,
        @Schema(description = "직영계약직 증빙 여부", example = "false") Boolean directContractEvidenceSubmitted,
        @Schema(description = "외주 공수합", example = "12.0") Double outsourcingWorkQuantitySum,
        @Schema(description = "외주 증빙 여부", example = "true") Boolean outsourcingEvidenceSubmitted,
        @Schema(description = "장비 총 가동 시간", example = "24.5") Double equipmentTotalHours,
        @Schema(description = "장비 증빙 여부", example = "true") Boolean equipmentEvidenceSubmitted,
        @Schema(description = "현장 사진 여부", example = "true") Boolean sitePhotoSubmitted,
        @Schema(description = "휘발유 총 주유량", example = "50.0") Double gasolineTotalAmount,
        @Schema(description = "경유 총 주유량", example = "150.0") Double dieselTotalAmount,
        @Schema(description = "요소수 총 주유량", example = "20.0") Double ureaTotalAmount,
        @Schema(description = "기타 총 주유량", example = "10.0") Double etcTotalAmount,
        @Schema(description = "유류 증빙 여부", example = "true") Boolean fuelEvidenceSubmitted,
        @Schema(description = "공사일보 작성 여부", example = "true") Boolean isConstructionReport,
        @Schema(description = "외주(공사) 항목 개수", example = "10") Integer outsourcingConstructionItemCount,
        @Schema(description = "외주(공사) 증빙 여부", example = "true") Boolean outsourcingConstructionEvidenceSubmitted,

        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportListResponse from(final DailyReport dailyReport) {
        return new DailyReportListResponse(
                dailyReport.getId(),
                dailyReport.getSite() != null ? SiteSimpleResponse.from(dailyReport.getSite()) : null,
                dailyReport.getSiteProcess() != null ? SiteProcessSimpleResponse.from(dailyReport.getSiteProcess())
                        : null,
                dailyReport.getReportDate(),
                dailyReport.getWeather() != null ? dailyReport.getWeather().getLabel() : null,
                dailyReport.getWeather(),
                dailyReport.getStatus(),
                dailyReport.getCompletedAt(),
                dailyReport.getEmployeeWorkQuantitySum(),
                dailyReport.getEmployeeEvidenceSubmitted(),
                dailyReport.getDirectContractWorkQuantitySum()
                        + dailyReport.getDirectContractOutsourcingWorkQuantitySum(),
                dailyReport.getDirectContractEvidenceSubmitted(),
                dailyReport.getOutsourcingWorkQuantitySum(),
                dailyReport.getOutsourcingEvidenceSubmitted(),
                dailyReport.getEquipmentTotalHours(),
                dailyReport.getEquipmentEvidenceSubmitted(),
                dailyReport.getSitePhotoSubmitted(),
                dailyReport.getGasolineTotalAmount(),
                dailyReport.getDieselTotalAmount(),
                dailyReport.getUreaTotalAmount(),
                dailyReport.getEtcTotalAmount(),
                dailyReport.getFuelEvidenceSubmitted(),
                dailyReport.getIsConstructionReport(),
                dailyReport.getOutsourcingConstructionItemCount(),
                dailyReport.getOutsourcingConstructionEvidenceSubmitted(),
                dailyReport.getCreatedAt(),
                dailyReport.getUpdatedAt());
    }
}

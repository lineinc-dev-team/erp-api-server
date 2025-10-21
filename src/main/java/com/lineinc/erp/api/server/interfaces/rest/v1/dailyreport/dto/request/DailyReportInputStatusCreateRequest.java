package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportInputStatusType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "투입현황 등록 요청")
public record DailyReportInputStatusCreateRequest(
        @NotBlank @Schema(description = "구분", example = "기술자") String category,
        @NotNull @Schema(description = "전일", example = "5") Long previousDayCount,
        @NotNull @Schema(description = "금일", example = "3") Long todayCount,
        @NotNull @Schema(description = "누계", example = "8") Long cumulativeCount,
        @NotNull @Schema(description = "투입현황 타입", example = "PERSONNEL") DailyReportInputStatusType type) {
}

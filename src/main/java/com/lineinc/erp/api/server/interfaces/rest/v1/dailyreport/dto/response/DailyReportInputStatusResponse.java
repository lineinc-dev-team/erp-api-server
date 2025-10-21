package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportInputStatus;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportInputStatusType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투입현황 응답")
public record DailyReportInputStatusResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "구분", example = "기술자") String category,
        @Schema(description = "전일", example = "5") Long previousDayCount,
        @Schema(description = "금일", example = "3") Long todayCount,
        @Schema(description = "누계", example = "8") Long cumulativeCount,
        @Schema(description = "투입현황 타입 코드", example = "PERSONNEL") DailyReportInputStatusType typeCode,
        @Schema(description = "투입현황 타입", example = "인원") String type) {

    public static DailyReportInputStatusResponse from(final DailyReportInputStatus inputStatus) {
        return new DailyReportInputStatusResponse(
                inputStatus.getId(),
                inputStatus.getCategory(),
                inputStatus.getPreviousDayCount(),
                inputStatus.getTodayCount(),
                inputStatus.getCumulativeCount(),
                inputStatus.getType(),
                inputStatus.getType() != null ? inputStatus.getType().getLabel() : null);
    }
}

package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportMaterialStatus;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportMaterialStatusType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자재현황 응답")
public record DailyReportMaterialStatusResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "품명", example = "시멘트") String materialName,
        @Schema(description = "단위", example = "kg") String unit,
        @Schema(description = "계획", example = "1000") Long plannedAmount,
        @Schema(description = "전일", example = "100") Long previousDayAmount,
        @Schema(description = "금일", example = "50") Long todayAmount,
        @Schema(description = "누계", example = "150") Long cumulativeAmount,
        @Schema(description = "잔여", example = "850") Long remainingAmount,
        @Schema(description = "자재현황 타입 코드", example = "COMPANY_SUPPLIED") DailyReportMaterialStatusType typeCode,
        @Schema(description = "자재현황 타입", example = "사급자재") String type) {

    public static DailyReportMaterialStatusResponse from(final DailyReportMaterialStatus materialStatus) {
        return new DailyReportMaterialStatusResponse(
                materialStatus.getId(),
                materialStatus.getMaterialName(),
                materialStatus.getUnit(),
                materialStatus.getPlannedAmount(),
                materialStatus.getPreviousDayAmount(),
                materialStatus.getTodayAmount(),
                materialStatus.getCumulativeAmount(),
                materialStatus.getRemainingAmount(),
                materialStatus.getType(),
                materialStatus.getType() != null ? materialStatus.getType().getLabel() : null);
    }
}

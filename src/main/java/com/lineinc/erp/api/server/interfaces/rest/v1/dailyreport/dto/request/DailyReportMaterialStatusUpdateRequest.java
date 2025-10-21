package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportMaterialStatusType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "출역일보 자재현황 수정 요청")
public record DailyReportMaterialStatusUpdateRequest(
        @NotNull @Schema(description = "자재현황 목록") java.util.List<MaterialStatusUpdateInfo> materialStatuses) {

    @Schema(description = "자재현황 수정 정보")
    public record MaterialStatusUpdateInfo(
            @Schema(description = "자재현황 ID", example = "1") Long id,
            @NotBlank @Schema(description = "품명", example = "시멘트") String materialName,
            @NotBlank @Schema(description = "단위", example = "kg") String unit,
            @NotNull @Schema(description = "계획", example = "1000") Long plannedAmount,
            @NotNull @Schema(description = "전일", example = "100") Long previousDayAmount,
            @NotNull @Schema(description = "금일", example = "50") Long todayAmount,
            @NotNull @Schema(description = "누계", example = "150") Long cumulativeAmount,
            @NotNull @Schema(description = "잔여", example = "850") Long remainingAmount,
            @NotNull @Schema(description = "자재현황 타입", example = "COMPANY_SUPPLIED") DailyReportMaterialStatusType type) {
    }
}

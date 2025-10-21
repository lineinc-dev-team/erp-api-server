package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "출역일보 주요공정 수정 요청")
public record DailyReportMainProcessUpdateRequest(
        @NotNull @Schema(description = "주요공정 목록") java.util.List<MainProcessUpdateInfo> mainProcesses) {

    @Schema(description = "주요공정 수정 정보")
    public record MainProcessUpdateInfo(
            @Schema(description = "주요공정 ID", example = "1") Long id,
            @NotBlank @Schema(description = "공정", example = "콘크리트 타설") String process,
            @NotBlank @Schema(description = "단위", example = "m³") String unit,
            @NotNull @Schema(description = "계약", example = "1000") Long contractAmount,
            @NotNull @Schema(description = "전일", example = "100") Long previousDayAmount,
            @NotNull @Schema(description = "금일", example = "50") Long todayAmount,
            @NotNull @Schema(description = "누계", example = "150") Long cumulativeAmount,
            @NotNull @Schema(description = "공정율", example = "15.0") Double processRate) {
    }
}

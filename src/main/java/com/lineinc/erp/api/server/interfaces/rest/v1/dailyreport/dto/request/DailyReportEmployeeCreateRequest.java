package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "출역일보 직원 등록 요청")
public record DailyReportEmployeeCreateRequest(
        @Schema(description = "인력 ID", example = "1") @NotNull Long laborId,
        @Schema(description = "작업내용", example = "기초공사") @NotBlank String workContent,
        @Schema(description = "공수", example = "8.0") @NotNull @Positive Double workQuantity,
        @Schema(description = "비고", example = "특별 지시사항") String memo) {
}

package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "출역일보 삭제 요청")
public record DeleteDailyReportsRequest(
        @NotEmpty
        @Schema(description = "삭제할 출역일보 ID 목록", example = "[1, 2, 3]")
        List<Long> dailyReportIds
) {
}

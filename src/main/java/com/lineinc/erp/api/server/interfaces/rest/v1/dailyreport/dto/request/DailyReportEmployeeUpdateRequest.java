package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;

@Schema(description = "출역일보 직원정보 수정 요청")
public record DailyReportEmployeeUpdateRequest(
        @Schema(description = "수정할 직원정보 목록") List<@Valid EmployeeUpdateInfo> employees) {

    @Schema(description = "직원정보 수정 내용")
    public record EmployeeUpdateInfo(
            @Schema(description = "ID", example = "1") Long id,
            @Schema(description = "노무인력 ID", example = "1") Long laborId,
            @Schema(description = "작업내용", example = "기초공사") String workContent,
            @Schema(description = "공수", example = "8.0") Double workQuantity,
            @Schema(description = "비고", example = "특별 지시사항") String memo) {
    }
}

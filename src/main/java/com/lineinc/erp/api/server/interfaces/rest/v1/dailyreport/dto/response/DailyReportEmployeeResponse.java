package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployee;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborNameResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 직원정보 응답")
public record DailyReportEmployeeResponse(
        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "작업내용", example = "기초 콘크리트 타설") String workContent,

        @Schema(description = "공수", example = "8.0") Double workQuantity,

        @Schema(description = "비고", example = "오전 작업") String memo,

        @Schema(description = "인력 정보") LaborNameResponse labor,

        @Schema(description = "등록일", example = "2024-01-15T10:00:00+09:00") OffsetDateTime createdAt,

        @Schema(description = "수정일", example = "2024-01-15T14:30:00+09:00") OffsetDateTime updatedAt) {

    public static DailyReportEmployeeResponse from(final DailyReportEmployee employee) {
        return new DailyReportEmployeeResponse(
                employee.getId(),
                employee.getWorkContent(),
                employee.getWorkQuantity(),
                employee.getMemo(),
                employee.getLabor() != null ? LaborNameResponse.from(employee.getLabor()) : null,
                employee.getCreatedAt(),
                employee.getUpdatedAt());
    }
}

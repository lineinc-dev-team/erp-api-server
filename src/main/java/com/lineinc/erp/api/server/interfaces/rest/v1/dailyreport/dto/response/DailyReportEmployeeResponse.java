package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportEmployee;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborNameResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 직원정보 응답")
public record DailyReportEmployeeResponse(
        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "작업내용", example = "기초 콘크리트 타설") String workContent,

        @Schema(description = "공수", example = "8.0") Double workQuantity,

        @Schema(description = "비고", example = "오전 작업") String memo,

        @Schema(description = "인력 정보") LaborNameResponse labor) {

    public static DailyReportEmployeeResponse from(DailyReportEmployee employee) {
        return new DailyReportEmployeeResponse(
                employee.getId(),
                employee.getWorkContent(),
                employee.getWorkQuantity(),
                employee.getMemo(),
                employee.getLabor() != null ? LaborNameResponse.from(employee.getLabor()) : null);
    }
}

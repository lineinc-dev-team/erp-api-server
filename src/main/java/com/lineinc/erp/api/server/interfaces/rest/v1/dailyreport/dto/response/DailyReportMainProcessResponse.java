package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportMainProcess;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주요공정 응답")
public record DailyReportMainProcessResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "공정", example = "콘크리트 타설") String process,
        @Schema(description = "단위", example = "m³") String unit,
        @Schema(description = "계약", example = "1000") Long contractAmount,
        @Schema(description = "전일", example = "100") Long previousDayAmount,
        @Schema(description = "금일", example = "50") Long todayAmount,
        @Schema(description = "누계", example = "150") Long cumulativeAmount,
        @Schema(description = "공정율", example = "15.0") Double processRate) {

    public static DailyReportMainProcessResponse from(final DailyReportMainProcess mainProcess) {
        return new DailyReportMainProcessResponse(
                mainProcess.getId(),
                mainProcess.getProcess(),
                mainProcess.getUnit(),
                mainProcess.getContractAmount(),
                mainProcess.getPreviousDayAmount(),
                mainProcess.getTodayAmount(),
                mainProcess.getCumulativeAmount(),
                mainProcess.getProcessRate());
    }
}

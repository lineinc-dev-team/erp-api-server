package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportWork;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 작업 응답")
public record DailyReportWorkResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "작업명", example = "콘크리트 타설") String workName,
        @Schema(description = "금일 여부", example = "true") Boolean isToday,
        @Schema(description = "작업 디테일 목록") java.util.List<DailyReportWorkDetailResponse> workDetails) {

    public static DailyReportWorkResponse from(final DailyReportWork work) {
        return new DailyReportWorkResponse(
                work.getId(),
                work.getWorkName(),
                work.getIsToday(),
                work.getWorkDetails().stream()
                        .map(DailyReportWorkDetailResponse::from)
                        .toList());
    }
}

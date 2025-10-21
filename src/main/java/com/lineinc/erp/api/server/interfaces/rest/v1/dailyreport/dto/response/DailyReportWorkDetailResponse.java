package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportWorkDetail;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "출역일보 작업 디테일 응답")
public record DailyReportWorkDetailResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "내용", example = "1층 슬래브 콘크리트 타설 작업") String content,
        @Schema(description = "인원 및 장비", example = "인원 5명, 믹서트럭 2대") String personnelAndEquipment) {

    public static DailyReportWorkDetailResponse from(final DailyReportWorkDetail workDetail) {
        return new DailyReportWorkDetailResponse(
                workDetail.getId(),
                workDetail.getContent(),
                workDetail.getPersonnelAndEquipment());
    }
}
package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportWorkContent;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "작업내용 응답")
public record DailyReportWorkContentResponse(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "작업명", example = "콘크리트 타설") String workName,
        @Schema(description = "내용", example = "1층 슬래브 콘크리트 타설 작업") String content,
        @Schema(description = "인원 및 장비", example = "인원 5명, 믹서트럭 2대") String personnelAndEquipment,
        @Schema(description = "금일 여부", example = "true") Boolean isToday) {

    public static DailyReportWorkContentResponse from(final DailyReportWorkContent workContent) {
        return new DailyReportWorkContentResponse(
                workContent.getId(),
                workContent.getWorkName(),
                workContent.getContent(),
                workContent.getPersonnelAndEquipment(),
                workContent.getIsToday());
    }
}

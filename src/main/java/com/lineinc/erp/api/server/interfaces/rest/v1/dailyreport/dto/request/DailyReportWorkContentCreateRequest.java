package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "작업내용 등록 요청")
public record DailyReportWorkContentCreateRequest(
        @NotBlank @Schema(description = "작업명", example = "콘크리트 타설") String workName,
        @NotBlank @Schema(description = "내용", example = "1층 슬래브 콘크리트 타설 작업") String content,
        @NotBlank @Schema(description = "인원 및 장비", example = "인원 5명, 믹서트럭 2대") String personnelAndEquipment,
        @NotNull @Schema(description = "금일 여부", example = "true") Boolean isToday) {
}

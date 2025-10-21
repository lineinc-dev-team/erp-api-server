package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "출역일보 작업 수정 요청")
public record DailyReportWorkUpdateRequest(
        @NotNull @Schema(description = "작업 목록") List<WorkUpdateInfo> works) {

    @Schema(description = "작업 수정 정보")
    public record WorkUpdateInfo(
            @Schema(description = "작업 ID", example = "1") Long id,
            @NotBlank @Schema(description = "작업명", example = "콘크리트 타설") String workName,
            @NotNull @Schema(description = "금일 여부", example = "true") Boolean isToday,
            @Schema(description = "작업 디테일 목록") List<WorkDetailUpdateInfo> workDetails) {

        @Schema(description = "작업 디테일 수정 정보")
        public record WorkDetailUpdateInfo(
                @Schema(description = "작업 디테일 ID", example = "1") Long id,
                @NotBlank @Schema(description = "내용", example = "1층 슬래브 콘크리트 타설 작업") String content,
                @NotBlank @Schema(description = "인원 및 장비", example = "인원 5명, 믹서트럭 2대") String personnelAndEquipment) {
        }
    }
}

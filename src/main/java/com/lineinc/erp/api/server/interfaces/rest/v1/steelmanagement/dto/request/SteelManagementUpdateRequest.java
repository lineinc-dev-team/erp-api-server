package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "강재 관리 수정 요청")
public record SteelManagementUpdateRequest(
        @NotNull @Schema(description = "현장 ID", example = "1") Long siteId,

        @NotNull @Schema(description = "공정 ID", example = "10") Long siteProcessId,

        @Schema(description = "용도", example = "철근 콘크리트 타설용") String usage,

        @Schema(description = "기간 시작일", example = "2024-07-01") LocalDate startDate,

        @Schema(description = "기간 종료일", example = "2024-07-31") LocalDate endDate,

        @Schema(description = "비고", example = "2024년 7월 강재 입출고") String memo,

        @Schema(description = "강재 관리 상세 품목 목록") List<SteelManagementDetailUpdateRequest> details,

        @Schema(description = "강재 관리 파일 목록") List<SteelManagementFileUpdateRequest> files) {
}

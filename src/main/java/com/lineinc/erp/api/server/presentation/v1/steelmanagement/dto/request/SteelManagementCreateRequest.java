package com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "강재 관리 등록 요청")
public record SteelManagementCreateRequest(
        @NotNull
        @Schema(description = "현장 ID", example = "1")
        Long siteId,

        @NotNull
        @Schema(description = "공정 ID", example = "10")
        Long siteProcessId,

        @Schema(description = "용도", example = "철근 콘크리트 타설용")
        String usage,

        @Schema(description = "비고", example = "2024년 7월 강재 입출고")
        String memo,

        @Schema(description = "강재 관리 상세 품목 목록")
        List<SteelManagementDetailCreateRequest> details,

        @Schema(description = "강재 관리 파일 목록")
        List<SteelManagementFileCreateRequest> files
) {
}

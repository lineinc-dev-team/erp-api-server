package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "현장 사진 등록 요청")
public record DailyReportFileCreateRequest(
        @Schema(description = "파일 URL", example = "https://example.com/photo.jpg") String fileUrl,

        @Schema(description = "원본 파일명", example = "현장사진.jpg") String originalFileName,

        @Schema(description = "설명", example = "기초공사 현장") @NotBlank String description,

        @Schema(description = "비고", example = "특별 지시사항") String memo) {
}

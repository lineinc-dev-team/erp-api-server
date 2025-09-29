package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "출역일보 문서 수정 요청")
public record DailyReportDocumentUpdateRequest(
        @Schema(description = "ID", example = "1") Long id,
        @NotBlank @Schema(description = "문서명", example = "계약서") String name,
        @Schema(description = "파일 URL", example = "https://example.com/document.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "계약서.pdf") String originalFileName,
        @Schema(description = "비고", example = "추가 문서") String memo) {
}

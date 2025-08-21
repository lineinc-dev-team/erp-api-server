package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력정보 첨부파일 수정 요청")
public record LaborFileUpdateRequest(
        @Schema(description = "파일 ID", example = "1") Long id,
        @Schema(description = "파일명", example = "근로계약서.pdf") String name,
        @Schema(description = "파일 URL", example = "https://example.com/files/contract.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "근로계약서_2024.pdf") String originalFileName,
        @Schema(description = "메모", example = "2024년 근로계약서") String memo) {
}

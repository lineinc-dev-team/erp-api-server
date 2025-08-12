package com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "자재관리 파일 수정 요청")
public record MaterialManagementFileUpdateRequest(
        @Schema(description = "자재관리 파일 상세 ID", example = "1")
        Long id,

        @Schema(description = "문서명", example = "납품확인서.pdf")
        String name,

        @Schema(description = "파일 URL", example = "https://s3.bucket.com/path/to/file.pdf")
        String fileUrl,

        @Schema(description = "원본 파일명", example = "납품확인서_2024_07_28.pdf")
        String originalFileName,

        @Schema(description = "비고", example = "1차 납품 관련 파일")
        String memo
) {
}

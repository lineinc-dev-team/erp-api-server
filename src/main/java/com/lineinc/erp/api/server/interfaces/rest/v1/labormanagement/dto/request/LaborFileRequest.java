package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "노무 파일 요청")
public record LaborFileRequest(
        @Schema(description = "문서명 (사용자가 지정하는 파일 이름)", example = "계약서") String name,

        @Schema(description = "S3 또는 외부 스토리지에 저장된 파일의 URL", example = "https://s3.amazonaws.com/bucket/file.pdf") String fileUrl,

        @Schema(description = "업로드된 파일의 원본 파일명", example = "contract.pdf") String originalFileName,

        @Schema(description = "파일에 대한 비고 또는 설명", example = "계약서 파일") String memo) {
}

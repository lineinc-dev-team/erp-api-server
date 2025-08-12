package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 파일 수정 요청")
public record ManagementCostFileUpdateRequest(
        @Schema(description = "관리비 파일 상세 ID", example = "1")
        Long id,

        @Schema(description = "문서명 (사용자가 지정하는 파일 이름)", example = "계약서.pdf")
        String name,

        @Schema(description = "파일 URL (S3 또는 외부 스토리지 경로)", example = "https://s3.example.com/bucket/contract.pdf")
        String fileUrl,

        @Schema(description = "원본 파일명", example = "original_contract.pdf")
        String originalFileName,

        @Schema(description = "비고 / 메모", example = "계약서 원본 파일")
        String memo
) {
}
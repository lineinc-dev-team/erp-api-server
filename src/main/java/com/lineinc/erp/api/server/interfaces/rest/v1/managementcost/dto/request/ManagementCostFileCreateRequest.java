package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 파일 등록 요청")
public record ManagementCostFileCreateRequest(
        @Schema(description = "문서명", example = "계약서") String name,
        @Schema(description = "파일 URL (S3 또는 외부 스토리지 경로)", example = "https://s3.example.com/bucket/contract.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "original_contract.pdf") String originalFileName,
        @Schema(description = "비고 / 메모", example = "계약서 원본 파일") String memo) {
}
package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리비 파일 수정 요청")
public record ManagementCostFileUpdateRequest(
        @Schema(description = "관리비 파일 상세 ID", example = "1") @NotNull Long id,
        @Schema(description = "문서명", example = "계약서") String name,
        @Schema(description = "파일 URL (S3 또는 외부 스토리지 경로)", example = "https://s3.example.com/bucket/contract.pdf") @NotNull String fileUrl,
        @Schema(description = "원본 파일명", example = "original_contract.pdf") @NotNull String originalFileName,
        @Schema(description = "비고 / 메모", example = "계약서 원본 파일") String memo) {
}
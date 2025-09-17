package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import com.lineinc.erp.api.server.domain.client.enums.ClientCompanyFileType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "발주처 파일 수정 요청")
public record ClientCompanyFileUpdateRequest(
        @Schema(description = "파일 ID", example = "1") Long id,
        @Schema(description = "문서명", example = "계약서_2024") @NotBlank String name,
        @Schema(description = "파일 접근용 URL", example = "https://s3.amazonaws.com/bucket/contract_2024.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "contract_2024.pdf") String originalFileName,
        @Schema(description = "파일 타입", example = "BUSINESS_LICENSE") @NotNull ClientCompanyFileType type,
        @Schema(description = "파일에 대한 메모", example = "계약서 사본") String memo) {
}
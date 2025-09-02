package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import com.lineinc.erp.api.server.domain.outsourcingcontract.enums.OutsourcingCompanyContractFileType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주업체 계약 첨부파일 수정 요청")
public record OutsourcingCompanyContractFileUpdateRequest(
        @Schema(description = "파일 ID", example = "1") Long id,

        @Schema(description = "문서명", example = "계약서") @NotBlank String name,

        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/path/to/contract.pdf") @NotBlank String fileUrl,

        @Schema(description = "원본 파일명", example = "계약서_2025.pdf") String originalFileName,

        @Schema(description = "파일 타입", example = "GUARANTEE") @NotNull OutsourcingCompanyContractFileType type,

        @Schema(description = "비고 / 메모", example = "2025년 계약서") String memo) {
}

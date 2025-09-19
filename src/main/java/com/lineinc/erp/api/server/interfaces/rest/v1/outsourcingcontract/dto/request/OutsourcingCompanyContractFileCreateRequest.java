package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractFileType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주업체 계약 첨부파일 등록 요청")
public record OutsourcingCompanyContractFileCreateRequest(
        @Schema(description = "문서명", example = "계약서") @NotBlank String name,

        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/path/to/contract.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "계약서_2025.pdf") String originalFileName,

        @Schema(description = "파일 타입", example = "CONTRACT") @NotNull OutsourcingCompanyContractFileType type,

        @Schema(description = "비고 / 메모", example = "2025년 계약서") String memo) {
}

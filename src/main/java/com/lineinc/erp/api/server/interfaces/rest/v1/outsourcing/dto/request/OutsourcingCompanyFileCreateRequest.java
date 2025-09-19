package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyFileType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주업체 첨부파일 등록 요청")
public record OutsourcingCompanyFileCreateRequest(
        @Schema(description = "문서명", example = "사업자등록증") @NotBlank String name,

        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/path/to/file.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "사업자등록증.pdf") String originalFileName,

        @Schema(description = "파일 타입", example = "BUSINESS_LICENSE") @NotNull OutsourcingCompanyFileType type,

        @Schema(description = "비고 / 메모", example = "2025년 기준 등록증") String memo) {
}

package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyFileType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "외주업체 첨부파일 수정 요청")
public record OutsourcingCompanyFileUpdateRequest(
        @Schema(description = "파일 ID", example = "123") Long id,

        @Schema(description = "문서명", example = "사업자등록증") @NotBlank String name,

        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/path/to/file.pdf") String fileUrl,

        @Schema(description = "파일 타입", example = "BUSINESS_LICENSE") OutsourcingCompanyFileType type,

        @Schema(description = "원본 파일명", example = "사업자등록증.pdf") String originalFileName,

        @Schema(description = "비고 / 메모", example = "2025년 기준 등록증") String memo) {
}

package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "외주업체 계약 인력 서류 등록 요청")
public record OutsourcingCompanyContractWorkerFileCreateRequest(
        @Schema(description = "파일 URL", example = "https://example.com/file.pdf") @NotBlank String fileUrl,

        @Schema(description = "원본 파일명", example = "safety_education.pdf") @NotBlank String originalFileName) {
}

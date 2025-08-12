package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "외주업체 계약 인력 서류 등록 요청")
public record OutsourcingCompanyContractWorkerFileCreateRequest(
        @NotBlank(message = "서류명은 필수입니다") @Schema(description = "서류명", example = "안전교육수료증") String name,

        @Schema(description = "파일 URL", example = "https://example.com/file.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "safety_education.pdf") String originalFileName,

        @Schema(description = "비고", example = "2024년도 안전교육") String memo) {
}

package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 인력 서류 수정 요청")
public record OutsourcingCompanyContractWorkerFileUpdateRequest(
        @Schema(description = "파일 ID", example = "1") Long id,

        @Schema(description = "파일 URL", example = "https://example.com/file.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "safety_education.pdf") String originalFileName) {
}

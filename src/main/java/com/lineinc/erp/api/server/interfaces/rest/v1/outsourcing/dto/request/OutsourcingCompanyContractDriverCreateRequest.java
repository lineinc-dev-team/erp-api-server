package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 운전자 등록 요청")
public record OutsourcingCompanyContractDriverCreateRequest(
        @Schema(description = "운전자명", example = "김운전")
        String name,

        @Schema(description = "기사자격증 파일명", example = "운전면허증.pdf")
        String driverLicenseName,

        @Schema(description = "기사자격증 파일 URL", example = "https://s3.amazonaws.com/bucket/path/to/license.pdf")
        String driverLicenseFileUrl,

        @Schema(description = "기사자격증 원본 파일명", example = "운전면허증_2025.pdf")
        String driverLicenseOriginalFileName,

        @Schema(description = "안전교육 파일명", example = "안전교육수료증.pdf")
        String safetyEducationName,

        @Schema(description = "안전교육 파일 URL", example = "https://s3.amazonaws.com/bucket/path/to/safety.pdf")
        String safetyEducationFileUrl,

        @Schema(description = "안전교육 원본 파일명", example = "안전교육수료증_2025.pdf")
        String safetyEducationOriginalFileName,

        @Schema(description = "기타서류 파일명", example = "건강진단서.pdf")
        String etcDocumentName,

        @Schema(description = "기타서류 파일 URL", example = "https://s3.amazonaws.com/bucket/path/to/health.pdf")
        String etcDocumentFileUrl,

        @Schema(description = "기타서류 원본 파일명", example = "건강진단서_2025.pdf")
        String etcDocumentOriginalFileName,

        @Schema(description = "비고", example = "경력 15년")
        String memo
) {
}

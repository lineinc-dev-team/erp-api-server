package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriverFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 기사 서류 정보 응답")
public record ContractDriverFileResponse(
        @Schema(description = "파일 ID", example = "1") Long id,
        @Schema(description = "서류 타입", example = "운전면허증") String documentType,
        @Schema(description = "서류 타입 코드", example = "운전면허증") String documentTypeCode,
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/files/driver_license.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "운전면허증.pdf") String originalFileName,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt) {

    public static ContractDriverFileResponse from(OutsourcingCompanyContractDriverFile file) {
        return new ContractDriverFileResponse(
                file.getId(),
                file.getDocumentType() != null ? file.getDocumentType().getLabel() : null,
                file.getDocumentType() != null ? file.getDocumentType().name() : null,
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }
}

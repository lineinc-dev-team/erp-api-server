package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractWorkerFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 인력 파일 정보 응답")
public record ContractWorkerFileResponse(
        @Schema(description = "파일 ID", example = "1") Long id,
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/files/worker_document.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "신분증사본.pdf") String originalFileName,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt) {

    public static ContractWorkerFileResponse from(OutsourcingCompanyContractWorkerFile file) {
        return new ContractWorkerFileResponse(
                file.getId(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }
}

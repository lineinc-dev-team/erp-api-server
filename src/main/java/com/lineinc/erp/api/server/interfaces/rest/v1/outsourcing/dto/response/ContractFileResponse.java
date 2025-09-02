package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "계약 첨부파일 응답")
public record ContractFileResponse(
        @Schema(description = "파일 ID", example = "1") Long id,
        @Schema(description = "파일명", example = "contract_document.pdf") String name,
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/files/contract_document.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "계약서_최종본.pdf") String originalFileName,
        @Schema(description = "메모", example = "계약서 최종본") String memo,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt) {
    public static ContractFileResponse from(OutsourcingCompanyContractFile file) {
        return new ContractFileResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo(),
                file.getCreatedAt(),
                file.getUpdatedAt());
    }
}

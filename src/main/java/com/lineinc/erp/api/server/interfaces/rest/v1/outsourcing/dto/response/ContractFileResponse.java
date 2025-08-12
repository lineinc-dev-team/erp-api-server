package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "계약 첨부파일 응답")
public record ContractFileResponse(
        @Schema(description = "파일명") String name,

        @Schema(description = "파일 URL") String fileUrl,

        @Schema(description = "원본 파일명") String originalFileName,

        @Schema(description = "메모") String memo) {

    public static ContractFileResponse from(OutsourcingCompanyContractFile file) {
        return new ContractFileResponse(
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo());
    }
}

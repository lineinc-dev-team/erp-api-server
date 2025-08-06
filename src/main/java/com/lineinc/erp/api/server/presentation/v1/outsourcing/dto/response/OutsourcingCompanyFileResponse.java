package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyFile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 파일 응답")
public record OutsourcingCompanyFileResponse(
        @Schema(description = "ID")
        Long id,

        @Schema(description = "문서명")
        String name,

        @Schema(description = "파일 URL")
        String fileUrl,

        @Schema(description = "원본 파일명")
        String originalFileName,

        @Schema(description = "비고")
        String memo
) {
    public static OutsourcingCompanyFileResponse from(OutsourcingCompanyFile file) {
        return new OutsourcingCompanyFileResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo()
        );
    }
}

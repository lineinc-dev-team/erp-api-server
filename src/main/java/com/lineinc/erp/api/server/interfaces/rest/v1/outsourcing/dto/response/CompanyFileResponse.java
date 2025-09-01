package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyFileType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 파일 응답")
public record CompanyFileResponse(
        @Schema(description = "ID") Long id,

        @Schema(description = "문서명") String name,

        @Schema(description = "파일 URL") String fileUrl,

        @Schema(description = "원본 파일명") String originalFileName,

        @Schema(description = "파일 타입") String type,

        @Schema(description = "파일 타입 코드") OutsourcingCompanyFileType typeCode,

        @Schema(description = "비고") String memo) {
    public static CompanyFileResponse from(OutsourcingCompanyFile file) {
        return new CompanyFileResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getType() != null ? file.getType().getLabel() : null,
                file.getType(),
                file.getMemo());
    }
}

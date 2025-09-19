package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompanyFile;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyFileType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 파일 응답")
public record FileResponse(
        @Schema(description = "파일 ID") Long id,
        @Schema(description = "파일명") String name,
        @Schema(description = "파일 타입") OutsourcingCompanyFileType type,
        @Schema(description = "원본 파일명") String originalFileName,
        @Schema(description = "비고") String memo) {
    public static FileResponse from(final OutsourcingCompanyFile file) {
        return new FileResponse(
                file.getId(),
                file.getName(),
                file.getType(),
                file.getOriginalFileName(),
                file.getMemo());
    }
}

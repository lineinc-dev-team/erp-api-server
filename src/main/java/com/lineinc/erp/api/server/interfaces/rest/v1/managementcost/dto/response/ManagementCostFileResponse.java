package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostFile;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostFileType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 파일 정보 응답")
public record ManagementCostFileResponse(
        @Schema(description = "파일 ID", example = "1") Long id,
        @Schema(description = "문서명", example = "계약서") String name,
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/.../file.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "invoice_june.pdf") String originalFileName,
        @Schema(description = "비고", example = "세금계산서 첨부") String memo,
        @Schema(description = "파일 유형", example = "기본") String type,
        @Schema(description = "파일 유형 코드", example = "BASIC") ManagementCostFileType typeCode) {
    public static ManagementCostFileResponse from(
            final ManagementCostFile file) {
        return new ManagementCostFileResponse(
                file.getId(),
                file.getName(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo(),
                file.getType() != null ? file.getType().getLabel() : null,
                file.getType());
    }
}

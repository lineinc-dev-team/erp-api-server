package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostFile;

@Schema(description = "관리비 파일 정보 응답")
public record ManagementCostFileResponse(
        @Schema(description = "파일 ID", example = "1") Long id,

        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/.../file.pdf") String fileUrl,

        @Schema(description = "원본 파일명", example = "invoice_june.pdf") String originalFileName,

        @Schema(description = "비고", example = "세금계산서 첨부") String memo) {
    public static ManagementCostFileResponse from(ManagementCostFile file) {
        return new ManagementCostFileResponse(
                file.getId(),
                file.getFileUrl(),
                file.getOriginalFileName(),
                file.getMemo());
    }
}
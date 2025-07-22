package com.lineinc.erp.api.server.presentation.v1.site.dto.response;

import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "현장 공정 정보 응답")
public record SiteProcessResponse(
        @Schema(description = "공정 ID", example = "1")
        Long id,

        @Schema(description = "공정명", example = "기초 공사")
        String name,

        @Schema(description = "사무실 전화번호")
        String officePhone,

        @Schema(description = "공정 상태", example = "IN_PROGRESS")
        String status,

        @Schema(description = "공정 비고", example = "공정 진행 중")
        String memo
) {


    public static SiteProcessResponse from(SiteProcess process) {
        return new SiteProcessResponse(
                process.getId(),
                process.getName(),
                process.getOfficePhone(),
                process.getStatus() != null ? process.getStatus().getLabel() : null,
                process.getMemo()
        );
    }
}

package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response;

import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "현장 공정 정보 응답")
public record SiteProcessResponse(
        @Schema(description = "공정 ID", example = "1")
        Long id,

        @Schema(description = "공정명", example = "기초 공사")
        String name,

        @Schema(description = "사무실 전화번호")
        String officePhone,

        @Schema(description = "공정 상태", example = "준비중")
        String status,

        @Schema(description = "공정 상태 코드", example = "IN_PROGRESS")
        SiteProcessStatus statusCode,

        @Schema(description = "공정 비고", example = "공정 진행 중")
        String memo
) {


    public static SiteProcessResponse from(SiteProcess process) {
        return new SiteProcessResponse(
                process.getId(),
                process.getName(),
                process.getOfficePhone(),
                process.getStatus() != null ? process.getStatus().getLabel() : null,
                process.getStatus() != null ? process.getStatus() : null,
                process.getMemo()
        );
    }

    @Schema(description = "간단한 현장 공정 응답")
    public static record SiteProcessSimpleResponse(
            @Schema(description = "공정 ID", example = "1")
            Long id,

            @Schema(description = "공정명", example = "기초 공사")
            String name
    ) {
        public static SiteProcessResponse.SiteProcessSimpleResponse from(SiteProcess siteProcess) {
            return new SiteProcessResponse.SiteProcessSimpleResponse(siteProcess.getId(), siteProcess.getName());
        }
    }
}

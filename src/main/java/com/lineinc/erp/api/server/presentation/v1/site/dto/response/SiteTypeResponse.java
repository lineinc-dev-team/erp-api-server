package com.lineinc.erp.api.server.presentation.v1.site.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현장 구분 응답")
public record SiteTypeResponse(
        @Schema(description = "현장 구분 ID", example = "1")
        Long id,

        @Schema(description = "현장 구분 코드", example = "CONSTRUCTION")
        String code,

        @Schema(description = "현장 구분 이름", example = "건축")
        String name
) {
}
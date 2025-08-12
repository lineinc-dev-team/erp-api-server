package com.lineinc.erp.api.server.presentation.v1.site.dto.response.sitefile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현장 파일 구분 응답")
public record SiteFileTypeResponse(
        @Schema(description = "현장 파일 구분 코드", example = "CONTRACT")
        String code,

        @Schema(description = "현장 파일 구분 이름", example = "계약서")
        String name
) {
}
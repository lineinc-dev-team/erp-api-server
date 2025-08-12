package com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record GradeResponse(
        @Schema(description = "직급 ID", example = "1")
        Long id,

        @Schema(description = "직급 이름", example = "대리")
        String name
) {
}

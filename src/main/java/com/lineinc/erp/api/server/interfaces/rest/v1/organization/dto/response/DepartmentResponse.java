package com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DepartmentResponse(
        @Schema(description = "부서 ID", example = "1")
        Long id,

        @Schema(description = "부서 이름", example = "경영지원팀")
        String name
) {
}

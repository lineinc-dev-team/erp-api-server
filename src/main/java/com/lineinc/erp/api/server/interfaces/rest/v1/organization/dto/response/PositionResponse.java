package com.lineinc.erp.api.server.presentation.v1.organization.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PositionResponse(
        @Schema(description = "직책 ID", example = "1")
        Long id,

        @Schema(description = "직책 이름", example = "팀장")
        String name
) {
}

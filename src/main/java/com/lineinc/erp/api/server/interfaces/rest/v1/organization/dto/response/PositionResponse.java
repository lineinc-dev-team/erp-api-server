package com.lineinc.erp.api.server.interfaces.rest.v1.organization.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PositionResponse(
        @Schema(description = "직책 ID", example = "1") Long id,

        @Schema(description = "직책 이름", example = "팀장") String name,

        @Schema(description = "직책 순서", example = "1") Integer order) {
}

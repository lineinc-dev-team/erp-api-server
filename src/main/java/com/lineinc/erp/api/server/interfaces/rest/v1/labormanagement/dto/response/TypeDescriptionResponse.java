package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력 구분 설명 응답")
public record TypeDescriptionResponse(
        @Schema(description = "인력 ID", example = "1") Long id,
        @Schema(description = "인력 구분 설명", example = "기술공") String typeDescription) {
}

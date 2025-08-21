package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력명 응답")
public record LaborNameResponse(
        @Schema(description = "인력 ID", example = "1") Long id,
        @Schema(description = "인력명", example = "홍길동") String name) {
}

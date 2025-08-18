package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재수불부 품명 응답")
public record SteelManagementNameResponse(
        @Schema(description = "품목 ID", example = "1") Long id,
        @Schema(description = "품명", example = "H빔") String name) {
}

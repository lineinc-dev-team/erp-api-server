package com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자재관리 품명 응답")
public record MaterialManagementNameResponse(
        @Schema(description = "품목 ID", example = "1") Long id,
        @Schema(description = "품명", example = "시멘트") String name) {
}

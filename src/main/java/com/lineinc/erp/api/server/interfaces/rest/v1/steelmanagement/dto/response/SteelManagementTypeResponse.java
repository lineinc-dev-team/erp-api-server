package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 관리 구분 응답")
public record SteelManagementTypeResponse(
        @Schema(description = "구분 코드", example = "ORDER") String code,

        @Schema(description = "구분명", example = "발주") String name) {
}

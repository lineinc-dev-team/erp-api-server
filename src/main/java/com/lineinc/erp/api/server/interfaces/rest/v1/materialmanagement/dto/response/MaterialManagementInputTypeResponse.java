package com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자재관리 투입구분 응답")
public record MaterialManagementInputTypeResponse(
        @Schema(description = "투입구분 코드", example = "MAJOR_PURCHASE") String code,

        @Schema(description = "투입구분 라벨", example = "주요자재(구매)") String label) {
}

package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 항목 설명 응답")
public record ItemDescriptionResponse(
        @Schema(description = "관리비 ID", example = "1") Long id,
        @Schema(description = "관리비 항목 설명", example = "6월 전기요금") String itemDescription) {
}

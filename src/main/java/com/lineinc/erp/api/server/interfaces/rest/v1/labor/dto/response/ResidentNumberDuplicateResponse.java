package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주민등록번호 중복 검사 응답")
public record ResidentNumberDuplicateResponse(
        @Schema(description = "중복 여부", example = "true") boolean isDuplicate) {
}


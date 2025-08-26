package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response;

import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력명 응답")
public record LaborNameResponse(
        @Schema(description = "인력 ID", example = "1") Long id,
        @Schema(description = "인력명", example = "홍길동") String name,
        @Schema(description = "노무인력 유형", example = "정직원") String type,
        @Schema(description = "노무인력 유형 코드", example = "REGULAR_EMPLOYEE") LaborType typeCode) {
}

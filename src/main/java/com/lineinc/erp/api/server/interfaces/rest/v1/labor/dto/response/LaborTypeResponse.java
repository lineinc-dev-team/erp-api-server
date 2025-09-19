package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response;

import com.lineinc.erp.api.server.domain.labor.enums.LaborType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "노무 구분 응답")
public record LaborTypeResponse(
        @Schema(description = "노무 구분 코드", example = "REGULAR_EMPLOYEE") String code,
        @Schema(description = "노무 구분 이름", example = "정직원") String name) {

    public static LaborTypeResponse from(LaborType laborType) {
        return new LaborTypeResponse(laborType.name(), laborType.getLabel());
    }
}

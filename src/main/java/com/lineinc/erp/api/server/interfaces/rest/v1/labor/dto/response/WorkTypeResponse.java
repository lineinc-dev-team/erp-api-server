package com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response;

import com.lineinc.erp.api.server.domain.labor.enums.LaborWorkType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공종 구분 응답")
public record WorkTypeResponse(
        @Schema(description = "공종 구분 코드", example = "FOREMAN") String code,
        @Schema(description = "공종 구분 이름", example = "반장") String name) {

    public static WorkTypeResponse from(final LaborWorkType workType) {
        return new WorkTypeResponse(workType.name(), workType.getLabel());
    }
}

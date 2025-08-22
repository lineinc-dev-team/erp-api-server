package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response;

import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력정보 요약 응답")
public record LaborSimpleResponse(
        @Schema(description = "인력 ID", example = "1") Long id,
        @Schema(description = "이름", example = "김철근") String name) {

    public static LaborSimpleResponse from(Labor labor) {
        return new LaborSimpleResponse(
                labor.getId(),
                labor.getName());
    }
}

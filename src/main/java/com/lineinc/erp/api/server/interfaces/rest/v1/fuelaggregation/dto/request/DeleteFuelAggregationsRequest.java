package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "유류집계 삭제 요청")
public record DeleteFuelAggregationsRequest(
        @NotEmpty @Schema(description = "삭제할 유류집계 ID 목록", example = "[1, 2, 3]") List<Long> fuelAggregationIds) {
}

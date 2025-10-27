package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 재료비 집계 조회 요청
 */
@ParameterObject
@Schema(description = "재료비 집계 조회 요청")
public record MaterialCostAggregationRequest(
        @NotNull @Schema(description = "현장 ID", example = "1") Long siteId,
        @NotNull @Schema(description = "공정 ID", example = "1") Long siteProcessId,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "년월 형식은 YYYY-MM 이어야 합니다.") @Schema(description = "조회월 (YYYY-MM)", example = "2025-10") String yearMonth) {
}

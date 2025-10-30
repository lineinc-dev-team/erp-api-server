package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 관리비 집계 조회 요청
 */
@ParameterObject
@Schema(description = "관리비 집계 조회 요청")
public record ManagementCostAggregationRequest(
        @NotNull @Schema(description = "현장 ID", example = "1") Long siteId,
        @NotNull @Schema(description = "공정 ID", example = "1") Long siteProcessId,
        @NotBlank @Schema(description = "조회월 (YYYY-MM)", example = "2025-10") String yearMonth) {
}

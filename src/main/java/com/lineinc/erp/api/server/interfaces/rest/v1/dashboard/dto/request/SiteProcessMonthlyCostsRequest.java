package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 현장 및 공정별 월별 비용 목록 조회 요청
 */
@ParameterObject
@Schema(description = "현장 및 공정별 월별 비용 목록 조회 요청")
public record SiteProcessMonthlyCostsRequest(
        @NotNull @Schema(description = "현장 ID", example = "1") Long siteId,
        @NotNull @Schema(description = "공정 ID", example = "1") Long siteProcessId) {
}

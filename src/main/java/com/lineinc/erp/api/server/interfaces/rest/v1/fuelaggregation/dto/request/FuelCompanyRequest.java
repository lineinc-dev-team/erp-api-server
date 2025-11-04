package com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@ParameterObject
@Schema(description = "유류업체 조회 요청")
public record FuelCompanyRequest(
        @Schema(description = "현장 ID", example = "1") @NotNull Long siteId,
        @Schema(description = "공정 ID", example = "1") @NotNull Long siteProcessId,
        @Schema(description = "집계일자", example = "2025-01-15") @NotNull LocalDate reportDate) {
}


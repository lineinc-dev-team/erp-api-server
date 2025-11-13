package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 외주(공사) 외주업체 목록 조회 요청
 */
@ParameterObject
@Schema(description = "외주(공사) 외주업체 목록 조회 요청")
public record ConstructionOutsourcingCompaniesRequest(
        @NotNull @Schema(description = "현장 ID", example = "1") Long siteId,
        @NotNull @Schema(description = "공정 ID", example = "1") Long siteProcessId) {
}

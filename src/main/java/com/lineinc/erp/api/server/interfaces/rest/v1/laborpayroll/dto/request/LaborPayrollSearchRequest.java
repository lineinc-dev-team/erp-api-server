package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request;

import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.media.Schema;

@ParameterObject
@Schema(description = "노무명세서 목록 조회 요청")
public record LaborPayrollSearchRequest(
        @Schema(description = "현장명", example = "A현장") String siteName,
        @Schema(description = "공정명", example = "철근공사") String processName,
        @Schema(description = "조회 년월 (YYYY-MM)", example = "2024-01") String yearMonth) {
}

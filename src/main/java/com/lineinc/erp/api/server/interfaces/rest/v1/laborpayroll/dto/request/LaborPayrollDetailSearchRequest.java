package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request;

import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;

@ParameterObject
@Schema(description = "노무명세서 상세 조회 요청")
public record LaborPayrollDetailSearchRequest(
        @Schema(description = "현장 ID", example = "1") @NotNull Long siteId,
        @Schema(description = "공정 ID", example = "1") @NotNull Long processId,
        @Schema(description = "조회 년월 (YYYY-MM)", example = "2024-01") @NotBlank String yearMonth,
        @Schema(description = "노무인력 타입", example = "REGULAR_EMPLOYEE") LaborType type) {
}

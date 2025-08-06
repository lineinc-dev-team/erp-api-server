package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 기본 공제 항목 응답")
public record OutsourcingCompanyDefaultDeductionsResponse(
        @Schema(description = "공제 항목 코드", example = "FOUR_INSURANCES")
        String code,

        @Schema(description = "공제 항목 이름", example = "4대 보험")
        String name
) {
}
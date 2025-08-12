package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체계약 유형 항목 응답")
public record ContractCategoryTypeResponse(
        @Schema(description = "유형 항목 코드", example = "MONTHLY")
        String code,

        @Schema(description = "유형 항목 이름", example = "월대")
        String name
) {
}
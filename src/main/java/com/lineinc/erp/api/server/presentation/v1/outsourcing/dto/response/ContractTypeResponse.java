package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체계약 구분 항목 응답")
public record ContractTypeResponse(
        @Schema(description = "구분 항목 코드", example = "SERVICE")
        String code,

        @Schema(description = "구분 항목 이름", example = "용역")
        String name
) {
}
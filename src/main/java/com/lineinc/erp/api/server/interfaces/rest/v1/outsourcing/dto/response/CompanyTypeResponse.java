package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 구분 항목 응답")
public record CompanyTypeResponse(
        @Schema(description = "구분 항목 코드", example = "SERVICE")
        String code,

        @Schema(description = "구분 항목 이름", example = "용역")
        String name
) {
}
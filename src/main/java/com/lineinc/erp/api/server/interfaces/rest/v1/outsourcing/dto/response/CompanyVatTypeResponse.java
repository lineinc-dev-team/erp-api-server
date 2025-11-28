package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 부가세 타입 응답")
public record CompanyVatTypeResponse(
        @Schema(description = "부가세 타입 코드", example = "NO_VAT") String code,
        @Schema(description = "부가세 타입 이름", example = "부가세 없음") String name) {
}

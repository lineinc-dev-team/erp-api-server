package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "세금계산서 발행조건 응답")
public record TaxInvoiceConditionResponse(
        @Schema(description = "세금계산서 발행조건 코드", example = "MONTH_END")
        String code,

        @Schema(description = "세금계산서 발행조건 이름", example = "월말일괄")
        String name
) {
}
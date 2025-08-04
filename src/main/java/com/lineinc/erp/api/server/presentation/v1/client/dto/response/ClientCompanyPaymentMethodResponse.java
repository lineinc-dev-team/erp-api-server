package com.lineinc.erp.api.server.presentation.v1.client.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "발주처 결제 방식 응답")
public record ClientCompanyPaymentMethodResponse(

        @Schema(description = "결제 방식 ID", example = "1")
        Long id,

        @Schema(description = "결제 방식 코드", example = "CASH")
        String code,

        @Schema(description = "결제 방식 이름", example = "현금")
        String name

) {
}
package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response;

import com.lineinc.erp.api.server.domain.clientcompany.enums.ClientCompanyPaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "발주처 결제 방식 응답")
public record ClientCompanyPaymentMethodResponse(
        @Schema(description = "결제 방식 코드", example = "CASH") String code,
        @Schema(description = "결제 방식 이름", example = "현금") String name) {

    public static ClientCompanyPaymentMethodResponse from(final ClientCompanyPaymentMethod paymentMethod) {
        return new ClientCompanyPaymentMethodResponse(
                paymentMethod.name(),
                paymentMethod.getLabel());
    }
}

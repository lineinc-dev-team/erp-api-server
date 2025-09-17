package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "간단한 발주처 응답")
public record ClientCompanySimpleResponse(
        @Schema(description = "발주처 ID", example = "123") Long id,
        @Schema(description = "발주처 이름", example = "삼성건설") String name,
        @Schema(description = "삭제 여부", example = "false") Boolean deleted) {

    public static ClientCompanySimpleResponse from(final ClientCompany clientCompany) {
        return new ClientCompanySimpleResponse(
                clientCompany.getId(),
                clientCompany.getName(),
                clientCompany.isDeleted());
    }
}

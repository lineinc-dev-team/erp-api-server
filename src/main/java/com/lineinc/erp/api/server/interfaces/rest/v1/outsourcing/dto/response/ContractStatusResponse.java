package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "계약 상태 응답")
public record ContractStatusResponse(
        @Schema(description = "계약 상태 코드", example = "IN_PROGRESS")
        String code,

        @Schema(description = "계약 상태 이름", example = "진행중")
        String name
) {
}
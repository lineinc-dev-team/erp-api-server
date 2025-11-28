package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 규격 응답")
public record SteelSpecificationResponse(
        @Schema(description = "규격 표기", example = "808 x 302 X 16.0 X 30") String specification,
        @Schema(description = "단위 중량", example = "0.2410") Double unitWeight) {
}

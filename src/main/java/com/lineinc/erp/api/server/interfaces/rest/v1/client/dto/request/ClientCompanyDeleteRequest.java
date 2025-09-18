package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "발주처 삭제 요청")
public record ClientCompanyDeleteRequest(
        @Schema(description = "삭제할 발주처 ID 목록", example = "[1, 2, 3]") @NotEmpty List<Long> clientCompanyIds) {
}

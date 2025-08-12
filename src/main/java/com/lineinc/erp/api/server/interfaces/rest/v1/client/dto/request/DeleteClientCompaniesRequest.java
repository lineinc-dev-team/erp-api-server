package com.lineinc.erp.api.server.presentation.v1.client.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "발주처 삭제 요청")
public record DeleteClientCompaniesRequest(
        @NotEmpty
        @Schema(description = "삭제할 발주처 ID 목록", example = "[1, 2, 3]")
        List<Long> clientCompanyIds
) {
}

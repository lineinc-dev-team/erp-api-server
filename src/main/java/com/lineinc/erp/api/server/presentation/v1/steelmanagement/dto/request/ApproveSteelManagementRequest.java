package com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "강재 관리 승인 요청")
public record ApproveSteelManagementRequest(
        @NotEmpty
        @Schema(description = "승인할 강재 관리 ID 목록", example = "[1, 2, 3]")
        List<Long> steelManagementIds
) {
}

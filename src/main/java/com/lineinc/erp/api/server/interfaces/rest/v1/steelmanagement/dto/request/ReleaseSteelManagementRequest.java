package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "강재 관리 반출 요청")
public record ReleaseSteelManagementRequest(
        @NotEmpty
        @Schema(description = "반출할 강재 관리 ID 목록", example = "[1, 2, 3]")
        List<Long> steelManagementIds
) {
}

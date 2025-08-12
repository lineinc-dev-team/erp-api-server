package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "관리비 삭제 요청")
public record DeleteManagementCostsRequest(
        @NotEmpty
        @Schema(description = "삭제할 관리비 ID 목록", example = "[1, 2, 3]")
        List<Long> managementCostIds
) {
}

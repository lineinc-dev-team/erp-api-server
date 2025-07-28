package com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "자재관리 삭제 요청")
public record DeleteMaterialManagementsRequest(
        @NotEmpty
        @Schema(description = "삭제할 자재관리 ID 목록", example = "[1, 2, 3]")
        List<Long> materialManagementIds
) {
}

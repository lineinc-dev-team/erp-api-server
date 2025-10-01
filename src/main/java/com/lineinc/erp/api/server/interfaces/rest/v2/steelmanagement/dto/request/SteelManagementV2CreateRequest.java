package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "강재수불부 V2 등록 요청")
public record SteelManagementV2CreateRequest(
        @Schema(description = "현장 ID", example = "1") @NotNull Long siteId,
        @Schema(description = "공정 ID", example = "1") @NotNull Long siteProcessId,
        @Schema(description = "강재수불부 상세 항목 목록") @Valid List<SteelManagementDetailV2CreateRequest> details) {
}

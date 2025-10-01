package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request;

import java.util.List;

import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(description = "강재수불부 V2 수정 요청")
public record SteelManagementV2UpdateRequest(
        @Schema(description = "현장 ID", example = "1") Long siteId,
        @Schema(description = "공정 ID", example = "10") Long siteProcessId,
        @Schema(description = "타입 (입고/출고/사장/고철)", example = "INCOMING") SteelManagementDetailV2Type type,
        @Valid @Schema(description = "강재수불부 상세 항목 목록") List<SteelManagementDetailV2UpdateRequest> details) {
}

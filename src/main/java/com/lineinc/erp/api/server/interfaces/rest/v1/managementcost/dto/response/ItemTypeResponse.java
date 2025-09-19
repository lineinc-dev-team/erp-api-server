package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 항목 타입 응답")
public record ItemTypeResponse(
        @Schema(description = "항목 타입 코드", example = "DEPOSIT") String code,
        @Schema(description = "항목 타입 라벨", example = "보증금") String name) {

    public static ItemTypeResponse from(final ManagementCostItemType itemType) {
        return new ItemTypeResponse(itemType.name(), itemType.getLabel());
    }
}

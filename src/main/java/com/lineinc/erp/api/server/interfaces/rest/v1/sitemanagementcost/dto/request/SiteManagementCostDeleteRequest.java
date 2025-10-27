package com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * 현장관리비 삭제 요청
 */
@Schema(description = "현장관리비 삭제 요청")
public record SiteManagementCostDeleteRequest(
        @NotEmpty @Schema(description = "삭제할 현장관리비 ID 목록", example = "[1, 2, 3]") List<Long> siteManagementCostIds) {
}

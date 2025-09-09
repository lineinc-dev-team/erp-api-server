package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request;

import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "현장 공정 수정 요청")
public record UpdateSiteProcessRequest(
        @Schema(description = "공정명", example = "기초 공사") @NotBlank String name,
        @Schema(description = "사무실 연락처", example = "02-123-4567") @NotBlank String officePhone,
        @Schema(description = "진행 상태", example = "NOT_STARTED") @NotNull SiteProcessStatus status,
        @Schema(description = "공정소장 ID", example = "2") @NotNull Long managerId,
        @Schema(description = "비고") String memo) {
}

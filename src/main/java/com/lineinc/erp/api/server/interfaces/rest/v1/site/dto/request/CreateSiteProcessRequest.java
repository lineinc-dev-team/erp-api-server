package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request;

import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import com.lineinc.erp.api.server.shared.validation.MultiConstraint;
import com.lineinc.erp.api.server.shared.validation.ValidatorType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "현장 공정 등록 요청")
public record CreateSiteProcessRequest(
        @NotBlank @Schema(description = "공정명", example = "기초 공사") String name,

        @NotBlank @MultiConstraint(type = ValidatorType.PHONE_OR_LANDLINE) @Schema(description = "사무실 연락처", example = "02-123-4567") String officePhone,

        @NotNull @Schema(description = "진행 상태", example = "NOT_STARTED") SiteProcessStatus status,

        @NotNull @Schema(description = "공정소장 ID", example = "2") Long managerId,

        @Schema(description = "비고") String memo) {
}

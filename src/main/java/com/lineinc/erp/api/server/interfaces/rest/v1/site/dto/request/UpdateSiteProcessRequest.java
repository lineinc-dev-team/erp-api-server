package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request;

import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import com.lineinc.erp.api.server.shared.validation.MultiConstraint;
import com.lineinc.erp.api.server.shared.validation.ValidatorType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현장 공정 등록 요청")
public record UpdateSiteProcessRequest(
        @Schema(description = "공정명", example = "기초 공사") String name,

        @MultiConstraint(type = ValidatorType.PHONE_OR_LANDLINE) @Schema(description = "사무실 연락처", example = "02-123-4567") String officePhone,

        @Schema(description = "진행 상태", example = "NOT_STARTED") SiteProcessStatus status,

        @Schema(description = "공정소장 ID", example = "2") Long managerId,

        @Schema(description = "비고") String memo) {
}

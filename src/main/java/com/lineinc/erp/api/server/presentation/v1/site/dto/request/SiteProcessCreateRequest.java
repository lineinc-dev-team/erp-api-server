package com.lineinc.erp.api.server.presentation.v1.site.dto.request;

import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "현장 공정 등록 요청")
public record SiteProcessCreateRequest(
        @NotBlank
        @Schema(description = "공정명", example = "기초 공사")
        String name,

        @Schema(description = "사무실 연락처")
        String officePhone,

        @NotNull
        @Schema(description = "진행 상태", example = "NOT_STARTED")
        SiteProcessStatus status,

        @Schema(description = "비고")
        String memo
) {
}

package com.lineinc.erp.api.server.presentation.v1.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "권한 그룹 생성 요청")
public record CreateRolesRequest(

        @NotBlank
        @Schema(description = "권한 그룹 이름", example = "현장 관리자")
        String name

) {
}
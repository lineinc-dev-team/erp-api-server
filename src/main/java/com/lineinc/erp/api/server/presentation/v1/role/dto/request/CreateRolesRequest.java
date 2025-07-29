package com.lineinc.erp.api.server.presentation.v1.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "권한 그룹 생성 요청")
public record CreateRolesRequest(
        @NotBlank
        @Schema(description = "권한 그룹 이름", example = "현장 관리자")
        String name,

        @Schema(description = "메모", example = "특별 권한 그룹입니다.")
        String memo,

        @Schema(description = "계정 ID 리스트", example = "[1, 2, 3]")
        List<Long> userIds
) {
}
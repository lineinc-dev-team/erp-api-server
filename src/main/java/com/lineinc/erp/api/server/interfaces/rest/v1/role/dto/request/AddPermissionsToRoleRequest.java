package com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "권한 그룹에 권한 수정 요청")
public record AddPermissionsToRoleRequest(
        @Schema(description = "추가할 권한 ID 목록", example = "[1, 2, 3]")
        @NotEmpty
        List<Long> permissionIds
) {
}
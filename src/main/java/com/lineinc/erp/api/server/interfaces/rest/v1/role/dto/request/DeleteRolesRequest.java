package com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "권한 그룹 삭제 요청")
public record DeleteRolesRequest(
        @NotEmpty
        @Schema(description = "삭제할 권한 그룹 ID 목록", example = "[1, 2, 3]")
        List<Long> roleIds
) {
}

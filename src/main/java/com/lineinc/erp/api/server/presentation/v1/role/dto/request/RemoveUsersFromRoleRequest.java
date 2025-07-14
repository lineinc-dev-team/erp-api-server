package com.lineinc.erp.api.server.presentation.v1.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "권한 그룹에서 제거할 유저 ID 목록")
public record RemoveUsersFromRoleRequest(

        @Schema(description = "삭제할 사용자 ID 목록", example = "[1, 2, 3]")
        List<Long> userIds
) {
}
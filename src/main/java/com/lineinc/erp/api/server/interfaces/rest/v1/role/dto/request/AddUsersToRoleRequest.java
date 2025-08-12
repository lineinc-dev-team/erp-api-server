package com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "권한 그룹에 추가할 유저 ID 및 메모 목록")
public record AddUsersToRoleRequest(

        @Schema(description = "추가할 사용자 ID 및 메모 목록")
        List<UserWithMemo> users
) {
    @Schema(description = "사용자 ID 및 메모 정보")
    public static record UserWithMemo(
            @Schema(description = "계정 ID", example = "1")
            Long userId,

            @Schema(description = "비고 / 메모", example = "현장 책임자")
            String memo
    ) {
    }
}
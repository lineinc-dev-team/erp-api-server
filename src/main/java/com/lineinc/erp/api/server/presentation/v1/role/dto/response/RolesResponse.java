package com.lineinc.erp.api.server.presentation.v1.role.dto.response;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "권한 그룹 리스트 응답")
public record RolesResponse(
        @Schema(description = "권한 그룹 ID", example = "1")
        Long id,

        @Schema(description = "권한 그룹 이름", example = "전체권한")
        String name,

        @Schema(description = "해당 권한 그룹에 속한 유저 수", example = "5")
        int userCount,

        @Schema(description = "생성일시", example = "2024-06-01T10:00:00")
        OffsetDateTime createdAt,

        @Schema(description = "수정일시", example = "2024-07-01T12:00:00")
        OffsetDateTime updatedAt,

        @Schema(description = "메모", example = "특별 권한 그룹입니다.")
        String memo
) {
    public static RolesResponse from(Role role) {
        int userCount = role.getUserRoles() == null ? 0 :
                (int) role.getUserRoles().stream()
                        .map(UserRole::getUser)
                        .distinct()
                        .count();

        return new RolesResponse(
                role.getId(),
                role.getName(),
                userCount,
                role.getCreatedAt(),
                role.getUpdatedAt(),
                role.getMemo()
        );
    }
}

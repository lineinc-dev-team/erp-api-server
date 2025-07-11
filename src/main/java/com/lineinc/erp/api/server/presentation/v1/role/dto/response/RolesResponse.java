package com.lineinc.erp.api.server.presentation.v1.role.dto.response;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "권한 그룹 리스트 응답")
public record RolesResponse(
        @Schema(description = "권한 그룹 ID", example = "1")
        Long id,

        @Schema(description = "권한 그룹 이름", example = "전체권한")
        String name,

        @Schema(description = "생성일시", example = "2024-06-01T10:00:00")
        OffsetDateTime createdAt,

        @Schema(description = "수정일시", example = "2024-07-01T12:00:00")
        OffsetDateTime updatedAt
) {
    public static RolesResponse from(Role role) {
        return new RolesResponse(role.getId(), role.getName(), role.getCreatedAt(), role.getUpdatedAt());
    }
}

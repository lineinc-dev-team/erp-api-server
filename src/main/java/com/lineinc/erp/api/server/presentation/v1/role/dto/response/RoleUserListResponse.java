package com.lineinc.erp.api.server.presentation.v1.role.dto.response;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "해당 권한 그룹에 속한 사용자 정보 응답")
public record RoleUserListResponse(
        @Schema(description = "사용자 ID", example = "123")
        Long id,

        @Schema(description = "로그인 ID", example = "admin01")
        String loginId,

        @Schema(description = "사용자 이름", example = "홍길동")
        String username,

        @Schema(description = "부서명", example = "총무팀")
        String department,

        @Schema(description = "등록일")
        OffsetDateTime createdAt,

        @Schema(description = "비고", example = "기타 메모")
        String memo
) {
    public static RoleUserListResponse from(User user, Long roleId) {
        UserRole userRole = user.getUserRoles().stream()
                .filter(ur -> ur.getRole().getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(ValidationMessages.ROLE_NOT_FOUND));

        return new RoleUserListResponse(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getDepartment() != null ? user.getDepartment().getName() : null,
                userRole.getCreatedAt(),
                userRole.getMemo()
        );
    }
}

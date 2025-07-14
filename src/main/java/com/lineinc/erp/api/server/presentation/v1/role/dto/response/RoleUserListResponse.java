package com.lineinc.erp.api.server.presentation.v1.role.dto.response;

import com.lineinc.erp.api.server.domain.user.entity.User;
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

        @Schema(description = "비고", example = "기타 메모")
        String memo,

        @Schema(description = "등록일")
        OffsetDateTime createdAt
) {
    public static RoleUserListResponse from(User user) {
        return new RoleUserListResponse(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getMemo(),
                user.getCreatedAt()
        );
    }
}

package com.lineinc.erp.api.server.presentation.v1.auth.dto.response;

import com.lineinc.erp.api.server.domain.roles.entity.Role;
import com.lineinc.erp.api.server.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "로그인된 사용자 정보 응답")
public record UserInfoResponse(
        @Schema(description = "사용자 ID", example = "123")
        Long id,

        @Schema(description = "로그인 ID", example = "admin01")
        String loginId,

        @Schema(description = "사용자 이름", example = "홍길동")
        String username,

        @Schema(description = "사용자 권한 목록", example = "[\"어드민\", \"매니저\"]")
        List<String> roles
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList()
        );
    }
}
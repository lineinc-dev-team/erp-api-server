package com.lineinc.erp.api.server.presentation.v1.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "사용자 생성 요청")
public record CreateUserRequest(
        @NotBlank
        @Schema(description = "로그인 아이디", example = "newuser01")
        String loginId,

        @NotBlank
        @Schema(description = "사용자 이름", example = "홍길동")
        String username,

        @Email
        @Schema(description = "이메일 주소", example = "user@example.com")
        String email,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @NotBlank
        @Schema(description = "비밀번호", example = "password123!")
        String password
) {
}
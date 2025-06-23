package com.lineinc.erp.api.server.presentation.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "비밀번호 초기화 요청")
public record ResetPasswordRequest(
        @NotBlank(message = "로그인 ID는 필수입니다.")
        @Schema(description = "로그인 ID", example = "test")
        String loginId
) {
}
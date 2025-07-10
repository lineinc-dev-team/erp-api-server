package com.lineinc.erp.api.server.presentation.v1.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "비밀번호 초기화 요청")
public record ResetPasswordRequest(
        @NotBlank
        @Schema(description = "로그인 ID", example = "test")
        String loginId
) {
}
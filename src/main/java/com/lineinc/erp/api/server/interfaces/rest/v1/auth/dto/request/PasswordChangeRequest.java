package com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "비밀번호 변경 요청")
public record PasswordChangeRequest(
        @NotBlank
        @Schema(description = "새 비밀번호", example = "new1234!")
        String newPassword
) {
}
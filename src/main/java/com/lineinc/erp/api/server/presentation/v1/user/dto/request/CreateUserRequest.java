package com.lineinc.erp.api.server.presentation.v1.user.dto.request;

import com.lineinc.erp.api.server.common.validation.MultiConstraint;
import com.lineinc.erp.api.server.common.validation.ValidatorType;
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

        @MultiConstraint(type = ValidatorType.PHONE)
        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @MultiConstraint(type = ValidatorType.LANDLINE_NUMBER)
        @Schema(description = "유선 전화번호", example = "02-123-4567")
        String landlineNumber,

        @NotBlank
        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @Schema(description = "사용자 활성화 여부", example = "true")
        boolean isActive
) {
}
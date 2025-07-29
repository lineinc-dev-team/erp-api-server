package com.lineinc.erp.api.server.presentation.v1.user.dto.request;

import com.lineinc.erp.api.server.common.validation.MultiConstraint;
import com.lineinc.erp.api.server.common.validation.ValidatorType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 수정 요청")
public record UpdateUserRequest(
        @Schema(description = "사용자 이름", example = "홍길동")
        String username,

        @Schema(description = "이메일 주소", example = "user@example.com")
        String email,

        @MultiConstraint(type = ValidatorType.PHONE)
        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @MultiConstraint(type = ValidatorType.LANDLINE_NUMBER)
        @Schema(description = "유선 전화번호", example = "02-123-4567")
        String landlineNumber,

        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @Schema(description = "사용자 활성화 여부", example = "true")
        Boolean isActive,

        @Schema(description = "부서 ID", example = "1")
        Long departmentId,

        @Schema(description = "직급 ID", example = "2")
        Long gradeId,

        @Schema(description = "직책 ID", example = "3")
        Long positionId
) {
}

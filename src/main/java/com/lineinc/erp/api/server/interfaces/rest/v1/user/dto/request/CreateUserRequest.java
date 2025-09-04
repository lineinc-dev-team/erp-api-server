package com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request;

import com.lineinc.erp.api.server.shared.validation.MultiConstraint;
import com.lineinc.erp.api.server.shared.validation.ValidatorType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유저 생성 요청")
public record CreateUserRequest(
        @NotBlank @Schema(description = "로그인 아이디", example = "newuser01") String loginId,

        @NotBlank @Schema(description = "사용자 이름", example = "홍길동") String username,

        @Email @NotBlank @Schema(description = "이메일 주소", example = "user@example.com") String email,

        @MultiConstraint(type = ValidatorType.PHONE) @NotBlank @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,

        @MultiConstraint(type = ValidatorType.LANDLINE_NUMBER) @NotBlank @Schema(description = "전화번호", example = "02-123-4567") String landlineNumber,

        @NotNull @Schema(description = "사용자 활성화 여부", example = "true") boolean isActive,

        @Schema(description = "본사 여부", example = "true") boolean isHeadOffice,

        @NotNull @Schema(description = "부서 ID", example = "1") Long departmentId,

        @NotNull @Schema(description = "직급 ID", example = "2") Long gradeId,

        @NotNull @Schema(description = "직책 ID", example = "3") Long positionId,

        @Schema(description = "비고", example = "추가 메모") String memo) {
}
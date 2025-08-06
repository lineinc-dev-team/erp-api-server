package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OutsourcingCompanyContactCreateRequest(
        @Schema(description = "담당자명", example = "홍길동")
        @NotBlank
        String name,

        @Schema(description = "부서", example = "시공팀")
        String department,

        @Schema(description = "직급", example = "대리")
        String position,

        @Schema(description = "전화번호", example = "02-123-4567")
        String landlineNumber,

        @Schema(description = "개인 휴대폰", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "이메일", example = "contact@outsourcing.com")
        @Email
        String email,

        @Schema(description = "비고", example = "안전관리 담당")
        String memo,

        @Schema(description = "대표 담당자 여부", example = "true")
        Boolean isMain
) {
}

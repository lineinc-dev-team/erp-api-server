package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import com.lineinc.erp.api.server.shared.validation.MultiConstraint;
import com.lineinc.erp.api.server.shared.validation.ValidatorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "발주처 담당자 등록 요청")
public record ClientCompanyContactCreateRequest(
        @NotBlank
        @Schema(description = "담당자명", example = "홍길동")
        String name,

        @NotBlank
        @Schema(description = "직급", example = "팀장")
        String position,

        @NotBlank
        @Schema(description = "부서", example = "영업팀")
        String department,

        @Schema(description = "대표 담당자 여부", example = "true")
        Boolean isMain,

        @MultiConstraint(type = ValidatorType.LANDLINE_NUMBER)
        @Schema(description = "전화번호", example = "02-123-5678")
        String landlineNumber,

        @NotBlank
        @MultiConstraint(type = ValidatorType.PHONE)
        @Schema(description = "개인 휴대폰", example = "010-1234-5678")
        String phoneNumber,

        @NotBlank
        @Email
        @Schema(description = "담당자 이메일", example = "hong@example.com")
        String email,

        @Schema(description = "비고 / 메모")
        String memo
) {
}
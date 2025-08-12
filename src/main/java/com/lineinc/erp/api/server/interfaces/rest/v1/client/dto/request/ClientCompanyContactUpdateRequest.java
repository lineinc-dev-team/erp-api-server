package com.lineinc.erp.api.server.presentation.v1.client.dto.request;

import com.lineinc.erp.api.server.shared.validation.MultiConstraint;
import com.lineinc.erp.api.server.shared.validation.ValidatorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "발주처 담당자 수정 요청")
public record ClientCompanyContactUpdateRequest(
        @Schema(description = "연락처 ID (수정 시 필수)", example = "123")
        Long id,

        @Schema(description = "담당자명", example = "홍길동")
        String name,

        @Schema(description = "직급 / 부서", example = "팀장")
        String position,

        @Schema(description = "부서", example = "영업팀")
        String department,

        @MultiConstraint(type = ValidatorType.LANDLINE_NUMBER)
        @Schema(description = "전화번호", example = "02-123-5678")
        String landlineNumber,

        @MultiConstraint(type = ValidatorType.PHONE)
        @Schema(description = "개인 휴대폰", example = "010-1234-5678")
        String phoneNumber,

        @Email
        @Schema(description = "담당자 이메일", example = "hong@example.com")
        String email,

        @Schema(description = "비고 / 메모")
        String memo,

        @Schema(description = "대표 담당자 여부", example = "true")
        Boolean isMain
) {
}
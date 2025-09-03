package com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request;

import com.lineinc.erp.api.server.shared.validation.MultiConstraint;
import com.lineinc.erp.api.server.shared.validation.ValidatorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "발주처 담당자 수정 요청")
public record ClientCompanyContactUpdateRequest(
        @Schema(description = "연락처 ID (수정 시 필수)", example = "123") @NotNull Long id,

        @Schema(description = "담당자명", example = "홍길동") @NotBlank String name,

        @Schema(description = "직급 / 부서", example = "팀장") @NotBlank String position,

        @Schema(description = "부서", example = "영업팀") @NotBlank String department,

        @MultiConstraint(type = ValidatorType.LANDLINE_NUMBER) @Schema(description = "전화번호", example = "02-123-5678") @NotBlank String landlineNumber,

        @MultiConstraint(type = ValidatorType.PHONE) @Schema(description = "개인 휴대폰", example = "010-1234-5678") @NotBlank String phoneNumber,

        @Email @Schema(description = "담당자 이메일", example = "hong@example.com") @NotBlank String email,

        @Schema(description = "비고 / 메모") String memo,

        @Schema(description = "대표 담당자 여부", example = "true") Boolean isMain) {
}
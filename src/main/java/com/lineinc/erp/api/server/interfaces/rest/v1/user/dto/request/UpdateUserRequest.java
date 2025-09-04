package com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request;

import java.util.List;

import com.lineinc.erp.api.server.shared.validation.MultiConstraint;
import com.lineinc.erp.api.server.shared.validation.ValidatorType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유저 수정 요청")
public record UpdateUserRequest(
        @Schema(description = "사용자 이름", example = "홍길동") @NotBlank String username,

        @Schema(description = "이메일 주소", example = "user@example.com") @NotBlank @Email String email,

        @MultiConstraint(type = ValidatorType.PHONE) @Schema(description = "개인 휴대폰", example = "010-1234-5678") @NotBlank String phoneNumber,

        @MultiConstraint(type = ValidatorType.LANDLINE_NUMBER) @Schema(description = "전화번호", example = "02-123-4567") @NotBlank String landlineNumber,

        @Schema(description = "사용자 활성화 여부", example = "true") @NotNull Boolean isActive,

        @Schema(description = "부서 ID", example = "1") @NotNull Long departmentId,

        @Schema(description = "직급 ID", example = "2") @NotNull Long gradeId,

        @Schema(description = "직책 ID", example = "3") @NotNull Long positionId,

        @Schema(description = "본사 여부", example = "true") @NotNull Boolean isHeadOffice,

        @Schema(description = "사용자 메모", example = "외주팀에서 이관됨") String memo,

        @Schema(description = "수정 이력 리스트") List<ChangeHistoryRequest> changeHistories) {
    public record ChangeHistoryRequest(
            @Schema(description = "수정 이력 번호", example = "1") Long id,

            @Schema(description = "변경 사유 또는 비고", example = "직급 변경에 따른 업데이트") String memo) {
    }
}

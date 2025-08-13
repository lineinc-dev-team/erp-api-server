package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "외주업체 계약 담당자 수정 요청")
public record OutsourcingCompanyContractContactUpdateRequest(
        @Schema(description = "담당자 ID", example = "1") Long id,

        @Schema(description = "이름", example = "김담당") @NotBlank String name,

        @Schema(description = "부서", example = "건설팀") String department,

        @Schema(description = "직급", example = "과장") String position,

        @Schema(description = "유선 전화번호", example = "02-123-4567") String landlineNumber,

        @Schema(description = "휴대폰 번호", example = "010-1234-5678") String phoneNumber,

        @Schema(description = "이메일", example = "manager@example.com") @Email String email,

        @Schema(description = "비고", example = "현장 총괄 담당자") String memo,

        @Schema(description = "대표 담당자 여부", example = "true") Boolean isMain) {
}

package com.lineinc.erp.api.server.presentation.v1.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "발주처 담당자 등록 요청 DTO")
public record ClientCompanyContactCreateRequest(
        @NotBlank
        @Schema(description = "담당자명", example = "홍길동")
        String name,

        @NotBlank
        @Schema(description = "직급 / 부서", example = "팀장")
        String position,

        @NotBlank
        @Schema(description = "담당자 연락처", example = "010-1234-5678")
        String phoneNumber,

        @NotBlank
        @Schema(description = "담당자 이메일", example = "hong@example.com")
        String email,

        @Schema(description = "비고 / 메모")
        String memo
) {
}
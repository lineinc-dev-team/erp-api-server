package com.lineinc.erp.api.server.presentation.v1.auth.dto;

import com.lineinc.erp.api.server.domain.users.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인된 사용자 정보 응답")
public record UserInfoResponse(
        @Schema(description = "사용자 ID", example = "123")
        Long id,

        @Schema(description = "로그인 ID", example = "admin01")
        String loginId,

        @Schema(description = "사용자 이름", example = "홍길동")
        String name,

        @Schema(description = "계정 유형", example = "ADMIN")
        AccountType accountType
) {
}
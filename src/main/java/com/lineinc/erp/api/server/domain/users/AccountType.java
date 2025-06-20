package com.lineinc.erp.api.server.domain.users;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "계정 유형", example = "ADMIN")
public enum AccountType {
    ADMIN,          // 관리자
    SITE_MANAGER,   // 현장담당
    CLIENT      // 외주사
}
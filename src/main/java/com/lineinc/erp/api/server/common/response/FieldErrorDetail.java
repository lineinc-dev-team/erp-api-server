package com.lineinc.erp.api.server.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 유효성 검사 실패 시 필드별 상세 정보
 */
@Schema(description = "유효성 검사 실패 시 필드별 에러 상세 정보")
public record FieldErrorDetail(
        @Schema(description = "유효성 검사 실패 필드명", example = "loginId")
        String field,

        @Schema(description = "에러 메시지", example = "로그인 ID는 필수입니다.")
        String message
) {
}
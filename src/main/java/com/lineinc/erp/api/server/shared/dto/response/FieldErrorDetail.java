package com.lineinc.erp.api.server.shared.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 유효성 검사 실패 시 필드별 상세 정보
 */
@Schema(description = "유효성 검사를 통과하지 못한 필드 정보")
public record FieldErrorDetail(
        @Schema(description = "유효성 검사 실패 필드명", example = "loginId") String field,
        @Schema(description = "유효성 검사 실패 메시지", example = "유저 ID는 필수 입력 필드입니다.") String message) {
}
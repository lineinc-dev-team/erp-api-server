package com.lineinc.erp.api.server.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * API 공통 에러 응답 형식
 */
@Schema(description = "API 에러 응답 형식")
public record ErrorResponse(
        @Schema(description = "HTTP 상태 코드", example = "400")
        int status,

        @Schema(description = "에러 메시지", example = "입력값이 유효하지 않습니다.")
        String message,

        @Schema(description = "필드별 에러 상세 정보 목록")
        List<FieldErrorDetail> errors
) {
    public static ErrorResponse of(int status, String message, List<FieldErrorDetail> errors) {
        return new ErrorResponse(status, message, errors);
    }
}
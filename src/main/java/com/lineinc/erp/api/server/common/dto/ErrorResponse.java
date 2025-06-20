package com.lineinc.erp.api.server.common.dto;

import lombok.Getter;

import java.util.List;

/**
 * API 공통 에러 응답 형식
 */
@Getter
public class ErrorResponse {

    private final int status;                    // HTTP 상태 코드
    private final String message;                // 에러 메시지
    private final List<FieldErrorDetail> errors; // 필드별 에러 상세 정보

    private ErrorResponse(int status, String message, List<FieldErrorDetail> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    /**
     * 정적 팩토리 메서드
     */
    public static ErrorResponse of(int status, String message, List<FieldErrorDetail> errors) {
        return new ErrorResponse(status, message, errors);
    }
}
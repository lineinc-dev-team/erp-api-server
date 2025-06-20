package com.lineinc.erp.api.server.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "공통 성공 응답 형식")
    public static class SuccessResponse<T> {

        @Schema(description = "HTTP 상태 코드", example = "200")
        private final int status = 200;

        @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
        private final String message;

        @Schema(description = "응답 데이터")
        private final T data;

        private SuccessResponse(String message, T data) {
            this.message = message;
            this.data = data;
        }

        public static <T> SuccessResponse<T> of(String message, T data) {
            return new SuccessResponse<>(message, data);
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }
    }
}
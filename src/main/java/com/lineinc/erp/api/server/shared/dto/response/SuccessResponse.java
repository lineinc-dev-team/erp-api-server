package com.lineinc.erp.api.server.shared.dto.response;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 성공 응답 형식")
public record SuccessResponse<T>(
        @Schema(description = "HTTP 상태 코드", example = "200") int status,

        @Schema(description = "응답 데이터") T data) {
    public SuccessResponse(final T data) {
        this(200, data);
    }

    public static <T> SuccessResponse<T> of(final T data) {
        return new SuccessResponse<>(data);
    }

    /**
     * ResponseEntity.ok(SuccessResponse.of(...))를 간소화하는 편의 메서드
     * 
     * @param data 응답 데이터
     * @return ResponseEntity<SuccessResponse<T>>
     */
    public static <T> ResponseEntity<SuccessResponse<T>> ok(final T data) {
        return ResponseEntity.ok(new SuccessResponse<>(data));
    }
}
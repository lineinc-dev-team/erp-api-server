package com.lineinc.erp.api.server.shared.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 성공 응답 형식")
public record SuccessResponse<T>(
        @Schema(description = "HTTP 상태 코드", example = "200") int status,

        @Schema(description = "응답 데이터") T data) {
    public SuccessResponse(T data) {
        this(200, data);
    }

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(data);
    }
}
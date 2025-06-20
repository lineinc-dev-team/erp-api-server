package com.lineinc.erp.api.server.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 성공 응답 형식")
public class SuccessResponse<T> {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int status = 200;

    @Schema(description = "응답 데이터")
    private final T data;

    private SuccessResponse(T data) {
        this.data = data;
    }

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(data);
    }
}
package com.lineinc.erp.api.server.common.response;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "페이징 정보")
public record PagingInfo(
        @Schema(description = "현재 페이지 번호", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int size,

        @Schema(description = "총 아이템 수", example = "123")
        long totalElements,

        @Schema(description = "총 페이지 수", example = "13")
        int totalPages
) {
    public static PagingInfo from(Page<?> page) {
        return new PagingInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
package com.lineinc.erp.api.server.shared.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "페이징 처리된 응답 형식")
public record PagingResponse<T>(
        @Schema(description = "페이징 정보") PagingInfo pageInfo,

        @Schema(description = "데이터 리스트") List<T> content) {
    public PagingResponse(final PagingInfo pageInfo, final List<T> content) {
        this.pageInfo = pageInfo;
        this.content = content;
    }

    public static <T> PagingResponse<T> from(final Page<T> page) {
        return new PagingResponse<>(PagingInfo.from(page), page.getContent());
    }
}

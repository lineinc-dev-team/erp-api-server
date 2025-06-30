package com.lineinc.erp.api.server.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "페이징 처리된 응답 형식")
public record PagingResponse<T>(
        @Schema(description = "페이징 정보")
        PagingInfo pageInfo,

        @Schema(description = "데이터 리스트")
        List<T> content
) {
    public PagingResponse(PagingInfo pageInfo, List<T> content) {
        this.pageInfo = pageInfo;
        this.content = content;
    }

    public static <T> PagingResponse<T> of(org.springframework.data.domain.Page<T> page) {
        PagingInfo info = new PagingInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return new PagingResponse<>(info, page.getContent());
    }
}


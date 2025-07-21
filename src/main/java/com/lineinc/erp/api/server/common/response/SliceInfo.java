package com.lineinc.erp.api.server.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Slice;

@Schema(description = "슬라이스 페이지 정보")
public record SliceInfo(
        @Schema(description = "현재 페이지 번호", example = "0")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int size,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {
    public static SliceInfo from(Slice<?> slice) {
        return new SliceInfo(
                slice.getNumber(),
                slice.getSize(),
                slice.hasNext()
        );
    }
}
package com.lineinc.erp.api.server.shared.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "슬라이스 처리된 응답 형식")
public record SliceResponse<T>(
        @Schema(description = "슬라이스 정보") SliceInfo sliceInfo,
        @Schema(description = "데이터 리스트") List<T> content) {
    public SliceResponse(final SliceInfo sliceInfo, final List<T> content) {
        this.sliceInfo = sliceInfo;
        this.content = content;
    }

    public static <T> SliceResponse<T> from(final Slice<T> slice) {
        return new SliceResponse<>(SliceInfo.from(slice), slice.getContent());
    }
}

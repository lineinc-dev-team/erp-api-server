package com.lineinc.erp.api.server.shared.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "슬라이스 처리된 응답 형식")
public record SliceResponse<T>(
        @Schema(description = "슬라이스 정보")
        SliceInfo sliceInfo,

        @Schema(description = "데이터 리스트")
        List<T> content
) {
    public SliceResponse(SliceInfo sliceInfo, List<T> content) {
        this.sliceInfo = sliceInfo;
        this.content = content;
    }
}

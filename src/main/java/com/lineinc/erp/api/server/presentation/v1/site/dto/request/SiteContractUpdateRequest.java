package com.lineinc.erp.api.server.presentation.v1.site.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "현장 계약 수정 요청")
public record SiteContractUpdateRequest(
        @Schema(description = "현장 계약 ID", example = "1")
        Long id,

        @NotBlank
        @Schema(description = "계약명", example = "전기공사 계약")
        String name,

        @Schema(description = "계약금액", example = "15000000")
        Long amount,

        @Schema(description = "비고")
        String memo,

        @Valid
        @Schema(description = "계약 관련 파일 목록")
        List<SiteFileUpdateRequest> files
) {
}

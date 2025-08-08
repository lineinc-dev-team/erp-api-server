package com.lineinc.erp.api.server.presentation.v1.site.dto.request.site;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "현장 삭제 요청")
public record DeleteSitesRequest(
        @NotEmpty
        @Schema(description = "삭제할 현장 ID 목록", example = "[1, 2, 3]")
        List<Long> siteIds
) {
}

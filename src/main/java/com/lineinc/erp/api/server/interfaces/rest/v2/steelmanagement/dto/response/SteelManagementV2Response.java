package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재수불부 V2 응답")
public record SteelManagementV2Response(
        @Schema(description = "ID", example = "1") Long id,

        @Schema(description = "현장 ID", example = "1") Long siteId,

        @Schema(description = "현장명", example = "서울지사 공사현장") String siteName,

        @Schema(description = "공정 ID", example = "1") Long siteProcessId,

        @Schema(description = "공정명", example = "철근 배근 작업") String processName,

        @Schema(description = "생성일시") OffsetDateTime createdAt,

        @Schema(description = "수정일시") OffsetDateTime updatedAt,

        @Schema(description = "상세 항목 목록") List<SteelManagementDetailV2Response> details) {

    public static SteelManagementV2Response from(final SteelManagementV2 entity) {
        return new SteelManagementV2Response(
                entity.getId(),
                entity.getSite() != null ? entity.getSite().getId() : null,
                entity.getSite() != null ? entity.getSite().getName() : null,
                entity.getSiteProcess() != null ? entity.getSiteProcess().getId() : null,
                entity.getSiteProcess() != null ? entity.getSiteProcess().getName() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDetails().stream()
                        .map(SteelManagementDetailV2Response::from)
                        .toList());
    }
}

package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response;

import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재수불부 V2 응답")
public record SteelManagementV2Response(
        @Schema(description = "ID", example = "1") Long id,
        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,
        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt) {

    public static SteelManagementV2Response from(final SteelManagementV2 entity) {
        return new SteelManagementV2Response(
                entity.getId(),
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}

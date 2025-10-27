package com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.sitemanagementcost.enums.SiteManagementCostChangeHistoryType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현장관리비 변경 이력 응답")
public record SiteManagementCostChangeHistoryResponse(
        @Schema(description = "변경 이력 ID", example = "1") Long id,
        @Schema(description = "변경 설명") String description,
        @Schema(description = "변경 상세 내역") @JsonProperty("getChanges") String getChanges,
        @Schema(description = "메모", example = "현장관리비 수정") String memo,
        @Schema(description = "생성 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime updatedAt,
        @Schema(description = "수정자", example = "관리자") String updatedBy,
        @Schema(description = "변경 유형", example = "현장관리비") String type,
        @Schema(description = "변경 유형 코드", example = "SITE_MANAGEMENT_COST") SiteManagementCostChangeHistoryType typeCode,
        @Schema(description = "수정 가능여부", example = "true") Boolean isEditable) {
    public static SiteManagementCostChangeHistoryResponse from(final SiteManagementCostChangeHistory history,
            final Long loginUserId) {
        return new SiteManagementCostChangeHistoryResponse(
                history.getId(),
                history.getDescription(),
                history.getChanges(),
                history.getMemo(),
                history.getCreatedAt(),
                history.getUpdatedAt(),
                history.getUpdatedBy(),
                history.getType() != null ? history.getType().getLabel() : null,
                history.getType(),
                history.getUser() != null && history.getUser().getId().equals(loginUserId));
    }
}


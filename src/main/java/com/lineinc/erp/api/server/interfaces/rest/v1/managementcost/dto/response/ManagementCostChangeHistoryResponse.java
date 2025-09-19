package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostChangeHistory;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeHistoryType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 변경 이력 응답")
public record ManagementCostChangeHistoryResponse(
        @Schema(description = "변경 이력 ID", example = "1") Long id,

        @Schema(description = "변경 상세 내역") @JsonProperty("getChanges") String getChanges,

        @Schema(description = "메모", example = "수정 사유") String memo,

        @Schema(description = "생성 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime createdAt,

        @Schema(description = "수정 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime updatedAt,

        @Schema(description = "수정자", example = "관리자") String updatedBy,

        @Schema(description = "변경 유형", example = "기본 정보") String type,

        @Schema(description = "변경 유형 코드", example = "BASIC") ManagementCostChangeHistoryType typeCode) {

    public static ManagementCostChangeHistoryResponse from(ManagementCostChangeHistory history) {
        return new ManagementCostChangeHistoryResponse(
                history.getId(),
                history.getChanges(),
                history.getMemo(),
                history.getCreatedAt(),
                history.getUpdatedAt(),
                history.getUpdatedBy(),
                history.getType().getLabel(),
                history.getType());
    }
}

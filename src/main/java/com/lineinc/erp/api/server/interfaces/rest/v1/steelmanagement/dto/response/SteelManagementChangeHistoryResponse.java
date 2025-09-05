package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementChangeHistory;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementChangeHistoryType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재수불부 변경 이력 응답")
public record SteelManagementChangeHistoryResponse(
        @Schema(description = "변경 이력 ID", example = "1") Long id,

        @Schema(description = "변경 상세 내역") @JsonProperty("getChanges") String getChanges,

        @Schema(description = "메모", example = "강재 정보 수정") String memo,

        @Schema(description = "생성 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime createdAt,

        @Schema(description = "수정 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime updatedAt,

        @Schema(description = "수정자", example = "관리자") String updatedBy,

        @Schema(description = "변경 유형", example = "기본정보") String type,

        @Schema(description = "변경 유형 코드", example = "BASIC") SteelManagementChangeHistoryType typeCode) {

    public static SteelManagementChangeHistoryResponse from(SteelManagementChangeHistory history) {
        return new SteelManagementChangeHistoryResponse(
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

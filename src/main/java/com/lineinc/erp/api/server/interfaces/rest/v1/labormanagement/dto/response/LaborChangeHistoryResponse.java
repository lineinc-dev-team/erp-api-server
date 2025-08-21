package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lineinc.erp.api.server.domain.labormanagement.entity.LaborChangeHistory;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborChangeType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "인력정보 변경 이력 응답")
public record LaborChangeHistoryResponse(
        @Schema(description = "변경 이력 ID", example = "1") Long id,

        @Schema(description = "변경 상세 내역") @JsonProperty("getChanges") String getChanges,

        @Schema(description = "메모", example = "이름 변경") String memo,

        @Schema(description = "생성 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime createdAt,

        @Schema(description = "수정 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime updatedAt,

        @Schema(description = "수정자", example = "관리자") String updatedBy,

        @Schema(description = "변경 유형", example = "기본정보") String type,

        @Schema(description = "변경 유형 코드", example = "BASIC") LaborChangeType typeCode) {
    public static LaborChangeHistoryResponse from(LaborChangeHistory history) {
        return new LaborChangeHistoryResponse(
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

package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingChangeHistory;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingChangeType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "외주업체 변경 이력 응답")
public record CompanyChangeHistoryResponse(
        @Schema(description = "변경 이력 ID", example = "1")
        Long id,

        @Schema(description = "변경 상세 내역")
        @JsonProperty("getChanges")
        String getChanges,

        @Schema(description = "메모", example = "조직 개편에 따른 이동")
        String memo,

        @Schema(description = "생성 일시", example = "2025-07-15T10:00:00+09:00")
        OffsetDateTime createdAt,

        @Schema(description = "수정 일시", example = "2025-07-15T10:00:00+09:00")
        OffsetDateTime updatedAt,

        @Schema(description = "수정자", example = "관리자")
        String updatedBy,

        @Schema(description = "변경 유형", example = "기본 정보")
        String type,

        @Schema(description = "변경 유형 코드", example = "CONTACT")
        OutsourcingChangeType typeCode
) {
    public static CompanyChangeHistoryResponse from(OutsourcingChangeHistory history) {
        return new CompanyChangeHistoryResponse(
                history.getId(),
                history.getChanges(),
                history.getMemo(),
                history.getCreatedAt(),
                history.getUpdatedAt(),
                history.getUpdatedBy(),
                history.getType().getLabel(),
                history.getType()
        );
    }
}
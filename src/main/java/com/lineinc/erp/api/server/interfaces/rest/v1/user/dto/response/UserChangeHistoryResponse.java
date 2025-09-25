package com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import com.lineinc.erp.api.server.domain.user.enums.UserChangeHistoryType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 변경 이력 응답")
public record UserChangeHistoryResponse(
        @Schema(description = "변경 이력 ID", example = "1") Long id,
        @Schema(description = "변경 상세 내역") @JsonProperty("getChanges") String getChanges,
        @Schema(description = "변경 설명") String description,
        @Schema(description = "메모", example = "조직 개편에 따른 이동") String memo,
        @Schema(description = "생성 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime createdAt,
        @Schema(description = "수정 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime updatedAt,
        @Schema(description = "수정자", example = "관리자") String updatedBy,
        @Schema(description = "변경 유형", example = "기본정보") String type,
        @Schema(description = "변경 유형 코드", example = "BASIC") UserChangeHistoryType typeCode,
        @Schema(description = "수정 가능 여부", example = "true") Boolean isEditable) {
    public static UserChangeHistoryResponse from(final UserChangeHistory history, final Long loggedInUserId) {
        return new UserChangeHistoryResponse(
                history.getId(),
                history.getChanges(),
                history.getDescription(),
                history.getMemo(),
                history.getCreatedAt(),
                history.getUpdatedAt(),
                history.getUpdatedBy(),
                history.getType() != null ? history.getType().getLabel() : null,
                history.getType(),
                history.getUpdatedByUser() != null && history.getUpdatedByUser().getId().equals(loggedInUserId));
    }
}
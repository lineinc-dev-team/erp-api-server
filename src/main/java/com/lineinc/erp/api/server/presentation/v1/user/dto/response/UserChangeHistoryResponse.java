package com.lineinc.erp.api.server.presentation.v1.user.dto.response;

import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "유저 변경 이력 응답")
public record UserChangeHistoryResponse(
        @Schema(description = "변경 이력 ID", example = "1")
        Long id,

        @Schema(description = "변경 상세 내역", example = "부서: 총무팀 → 재무팀, 직급: 사원 → 대리")
        String changeDetail,

        @Schema(description = "메모", example = "조직 개편에 따른 이동")
        String memo,

        @Schema(description = "생성 일시", example = "2025-07-15T10:00:00+09:00")
        OffsetDateTime createdAt,

        @Schema(description = "수정 일시", example = "2025-07-15T10:00:00+09:00")
        OffsetDateTime updatedAt,

        @Schema(description = "수정자", example = "관리자")
        String updatedBy
) {
    public static UserChangeHistoryResponse from(UserChangeHistory history) {
        return new UserChangeHistoryResponse(
                history.getId(),
                history.getChangeDetail(),
                history.getMemo(),
                history.getCreatedAt(),
                history.getUpdatedAt(),
                history.getUpdatedBy()
        );
    }
}
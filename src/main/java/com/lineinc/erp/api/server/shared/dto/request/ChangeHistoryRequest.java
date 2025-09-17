package com.lineinc.erp.api.server.shared.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공통 변경 이력 요청 DTO
 * 모든 엔티티의 변경 이력 관리에 재사용 가능
 */
@Schema(description = "변경 이력 요청")
public record ChangeHistoryRequest(
        @Schema(description = "변경 이력 ID", example = "1") Long id,
        @Schema(description = "변경 사유 또는 비고", example = "변경에 따른 업데이트") String memo) {
}

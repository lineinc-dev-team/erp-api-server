package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "노무명세서 변경이력 수정 요청")
public record LaborPayrollChangeHistoryUpdateRequest(
        @Schema(description = "비고", example = "수정 사유 설명") String memo) {
}

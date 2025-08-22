package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "전도금 상세 수정 요청")
public record ManagementCostKeyMoneyDetailUpdateRequest(
        @Schema(description = "전도금 상세 ID", example = "1") Long id,
        @Schema(description = "계좌", example = "건설업무") String account,
        @Schema(description = "사용목적", example = "현장 운영비") String purpose,
        @Schema(description = "인원 수", example = "5") Integer personnelCount,
        @Schema(description = "금액", example = "500000") Long amount,
        @Schema(description = "비고", example = "1차 전도금") String memo) {
}

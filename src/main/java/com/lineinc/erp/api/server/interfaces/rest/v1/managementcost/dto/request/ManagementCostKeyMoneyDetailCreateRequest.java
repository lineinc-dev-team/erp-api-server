package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 전도금 상세 등록 요청")
public record ManagementCostKeyMoneyDetailCreateRequest(
        @Schema(description = "계정", example = "건설업무") String account,

        @Schema(description = "사용목적", example = "현장 운영비") String purpose,

        @Schema(description = "인원수", example = "5") Integer personnelCount,

        @Schema(description = "금액", example = "1000000") Long amount,

        @Schema(description = "비고", example = "1차 전도금") String memo) {
}

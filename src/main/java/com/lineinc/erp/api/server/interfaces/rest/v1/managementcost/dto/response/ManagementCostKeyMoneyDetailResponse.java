package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostKeyMoneyDetail;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "전도금 상세 정보 응답")
public record ManagementCostKeyMoneyDetailResponse(
        @Schema(description = "전도금 상세 ID", example = "1") Long id,
        @Schema(description = "계좌", example = "신한은행 110-123456-789") String account,
        @Schema(description = "사용목적", example = "현장 운영비") String purpose,
        @Schema(description = "인원 수", example = "5") Integer personnelCount,
        @Schema(description = "금액", example = "500000") Long amount,
        @Schema(description = "공제여부", example = "true") Boolean isDeductible,
        @Schema(description = "비고", example = "1차 전도금") String memo) {
    public static ManagementCostKeyMoneyDetailResponse from(final ManagementCostKeyMoneyDetail detail) {
        return new ManagementCostKeyMoneyDetailResponse(
                detail.getId(),
                detail.getAccount(),
                detail.getPurpose(),
                detail.getPersonnelCount(),
                detail.getAmount(),
                detail.getIsDeductible(),
                detail.getMemo());
    }
}

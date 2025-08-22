package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "식대 상세 정보 응답")
public record ManagementCostMealFeeDetailResponse(
        @Schema(description = "식대 상세 ID", example = "1") Long id,

        @Schema(description = "직종", example = "철근") String workType,

        @Schema(description = "인력 ID", example = "1") Long laborId,

        @Schema(description = "이름", example = "김철근") String name,

        @Schema(description = "아침 식수 개수", example = "3") Integer breakfastCount,

        @Schema(description = "점심 식수 개수", example = "5") Integer lunchCount,

        @Schema(description = "단가", example = "8000") Long unitPrice,

        @Schema(description = "금액", example = "80000") Long amount,

        @Schema(description = "비고", example = "현장 식대") String memo) {
    public static ManagementCostMealFeeDetailResponse from(ManagementCostMealFeeDetail detail) {
        return new ManagementCostMealFeeDetailResponse(
                detail.getId(),
                detail.getWorkType(),
                detail.getLabor() != null ? detail.getLabor().getId() : null,
                detail.getName(),
                detail.getBreakfastCount(),
                detail.getLunchCount(),
                detail.getUnitPrice(),
                detail.getAmount(),
                detail.getMemo());
    }
}

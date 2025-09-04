package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "식대 상세 수정 요청")
public record ManagementCostMealFeeDetailUpdateRequest(
        @Schema(description = "식대 상세 ID", example = "1") @NotNull Long id,
        @Schema(description = "직종", example = "철근") @NotNull String workType,
        @Schema(description = "인력 ID", example = "1") Long laborId,
        @Schema(description = "이름", example = "김철근") @NotNull String name,
        @Schema(description = "아침 식수 개수", example = "3") @NotNull Integer breakfastCount,
        @Schema(description = "점심 식수 개수", example = "5") @NotNull Integer lunchCount,
        @Schema(description = "단가", example = "8000") @NotNull Long unitPrice,
        @Schema(description = "금액", example = "80000") @NotNull Long amount,
        @Schema(description = "비고", example = "현장 식대") String memo) {
}

package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리비 식대 상세 - 외주인력 등록 요청")
public record ManagementCostMealFeeDetailOutsourcingContractCreateRequest(
        @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "인력 ID", example = "1") Long laborId,
        @Schema(description = "조식 개수", example = "20") @NotNull Integer breakfastCount,
        @Schema(description = "중식 개수", example = "25") @NotNull Integer lunchCount,
        @Schema(description = "석식 개수", example = "5") Integer dinnerCount,
        @Schema(description = "단가", example = "8000") @NotNull Long unitPrice,
        @Schema(description = "금액", example = "200000") @NotNull Long amount,
        @Schema(description = "비고", example = "6월 식대") String memo) {
}

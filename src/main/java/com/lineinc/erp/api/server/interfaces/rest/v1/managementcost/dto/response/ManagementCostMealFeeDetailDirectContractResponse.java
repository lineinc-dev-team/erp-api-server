package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailDirectContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 식대 상세 - 직영 정보 응답")
public record ManagementCostMealFeeDetailDirectContractResponse(
        @Schema(description = "식대 상세 ID", example = "1") Long id,
        @Schema(description = "조식 개수", example = "3") Integer breakfastCount,
        @Schema(description = "중식 개수", example = "5") Integer lunchCount,
        @Schema(description = "석식 개수", example = "5") Integer dinnerCount,
        @Schema(description = "단가", example = "8000") Long unitPrice,
        @Schema(description = "금액", example = "80000") Long amount,
        @Schema(description = "비고", example = "현장 식대") String memo,
        @Schema(description = "인력 정보") LaborSimpleResponse labor) {
    public static ManagementCostMealFeeDetailDirectContractResponse from(
            final ManagementCostMealFeeDetailDirectContract detail) {
        return new ManagementCostMealFeeDetailDirectContractResponse(
                detail.getId(),
                detail.getBreakfastCount(),
                detail.getLunchCount(),
                detail.getDinnerCount(),
                detail.getUnitPrice(),
                detail.getAmount(),
                detail.getMemo(),
                detail.getLabor() != null ? LaborSimpleResponse.from(detail.getLabor()) : null);
    }
}


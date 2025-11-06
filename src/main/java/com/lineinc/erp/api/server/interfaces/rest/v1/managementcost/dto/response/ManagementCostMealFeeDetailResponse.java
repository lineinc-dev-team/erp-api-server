package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 식대 상세 - 직원 정보 응답")
public record ManagementCostMealFeeDetailResponse(
        @Schema(description = "식대 상세 ID", example = "1") Long id,
        @Schema(description = "직종", example = "철근") String workType,
        @Schema(description = "이름", example = "김철근") String name,
        @Schema(description = "아침 식수 개수", example = "3") Integer breakfastCount,
        @Schema(description = "점심 식수 개수", example = "5") Integer lunchCount,
        @Schema(description = "단가", example = "8000") Long unitPrice,
        @Schema(description = "금액", example = "80000") Long amount,
        @Schema(description = "비고", example = "현장 식대") String memo,
        @Schema(description = "인력 정보") LaborSimpleResponse labor) {
    public static ManagementCostMealFeeDetailResponse from(final ManagementCostMealFeeDetail detail) {
        return new ManagementCostMealFeeDetailResponse(
                detail.getId(),
                detail.getWorkType(),
                detail.getName(),
                detail.getBreakfastCount(),
                detail.getLunchCount(),
                detail.getUnitPrice(),
                detail.getAmount(),
                detail.getMemo(),
                detail.getLabor() != null ? LaborSimpleResponse.from(detail.getLabor()) : null);
    }
}

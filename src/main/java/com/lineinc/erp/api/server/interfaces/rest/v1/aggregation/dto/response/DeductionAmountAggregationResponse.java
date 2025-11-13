package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 공제금액 집계 응답
 */
@Schema(description = "공제금액 집계 응답")
public record DeductionAmountAggregationResponse(
        @Schema(description = "식대 공제금액") DeductionDetail mealFee,
        @Schema(description = "간식대 공제금액") DeductionDetail snackFee,
        @Schema(description = "유류대 공제금액") DeductionDetail fuelFee) {

    /**
     * 공제금액 상세
     */
    @Schema(description = "공제금액 상세")
    public record DeductionDetail(
            @Schema(description = "전회까지 청구내역") BillingDetail previousBilling,
            @Schema(description = "금회 청구내역") BillingDetail currentBilling) {
    }

    /**
     * 청구내역 상세
     */
    @Schema(description = "청구내역 상세")
    public record BillingDetail(
            @Schema(description = "총 금액", example = "5000000") Long totalAmount) {
    }
}

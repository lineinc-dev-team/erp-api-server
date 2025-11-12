package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 본사 집계 응답 DTO
 */
@Schema(description = "본사 집계 응답")
public record HeadquarterAggregationResponse(
        @Schema(description = "비용 항목 요약 목록") List<CostSummary> costSummaries) {

    /**
     * 비용 항목별 요약 정보
     */
    @Schema(description = "비용 항목 요약 정보")
    public record CostSummary(
            @Schema(description = "공종명", example = "기초공사") String processName,
            @Schema(description = "전회까지 합계") BillingSummary previousSummary,
            @Schema(description = "금회 합계") BillingSummary currentSummary) {
    }

    /**
     * 전회/금회 합계 정보
     */
    @Schema(description = "전회/금회 합계 정보")
    public record BillingSummary(
            @Schema(description = "공급가 합계", example = "1000000") Long supplyPrice,
            @Schema(description = "부가세 합계", example = "100000") Long vat,
            @Schema(description = "공제금액 합계", example = "50000") Long deductionAmount,
            @Schema(description = "계 합계", example = "1050000") Long total) {
    }
}

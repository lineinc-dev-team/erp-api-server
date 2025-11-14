package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractListResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 외주(공사) 집계 응답
 */
@Schema(description = "외주(공사) 집계 응답")
public record ConstructionOutsourcingAggregationResponse(
        @Schema(description = "외주(공사) 집계 항목 목록") List<ConstructionOutsourcingAggregationItem> items) {

    /**
     * 외주(공사) 집계 항목
     */
    @Schema(description = "외주(공사) 집계 항목")
    public record ConstructionOutsourcingAggregationItem(
            @Schema(description = "외주업체 계약 정보") ContractListResponse.ContractSimpleResponse outsourcingCompanyContract,
            @Schema(description = "전회까지 청구내역") BillingDetail previousBilling,
            @Schema(description = "금회 청구내역") BillingDetail currentBilling) {
    }

    /**
     * 청구내역 상세
     */
    @Schema(description = "청구내역 상세")
    public record BillingDetail(
            @Schema(description = "공급가액", example = "4500000") long supplyPrice,
            @Schema(description = "부가세", example = "450000") long vat,
            @Schema(description = "공제금액", example = "100000") long deduction,
            @Schema(description = "총액", example = "4950000") long total) {
    }
}

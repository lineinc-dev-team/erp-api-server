package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionGroupResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 외주(공사) 집계 상세 응답
 */
@Schema(description = "외주(공사) 집계 상세 응답")
public record ConstructionOutsourcingAggregationDetailResponse(
        @Schema(description = "외주(공사) 집계 항목 목록") List<ConstructionOutsourcingAggregationDetailItem> items) {

    /**
     * 외주(공사) 집계 상세 항목
     */
    @Schema(description = "외주(공사) 집계 상세 항목")
    public record ConstructionOutsourcingAggregationDetailItem(
            @Schema(description = "외주업체계약 공사그룹 정보") ContractConstructionGroupResponse.ContractConstructionGroupSimpleResponseForDailyReport outsourcingCompanyContractConstructionGroup,
            @Schema(description = "외주업체계약 공사항목 정보") ContractConstructionResponse.ContractConstructionSimpleResponse outsourcingCompanyContractConstruction,
            @Schema(description = "전회까지 청구내역") BillingDetail previousBilling,
            @Schema(description = "금회 청구내역") BillingDetail currentBilling) {
    }

    /**
     * 청구내역 상세
     */
    @Schema(description = "청구내역 상세")
    public record BillingDetail(
            @Schema(description = "총 수량", example = "100") Integer totalQuantity,
            @Schema(description = "총 금액", example = "5000000") Long totalAmount) {
    }
}

package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractConstructionGroupResponse.ContractConstructionGroupSimpleResponseForDailyReport;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 외주(공사) 집계 상세 응답
 */
@Schema(description = "외주(공사) 집계 상세 응답")
public record ConstructionOutsourcingAggregationDetailResponse(
        @Schema(description = "외주(공사) 집계 그룹 목록") List<ConstructionGroupItem> groups) {

    /**
     * 외주(공사) 집계 그룹 항목
     */
    @Schema(description = "외주(공사) 집계 그룹 항목")
    public record ConstructionGroupItem(
            @Schema(description = "외주업체계약 공사항목 그룹 정보") ContractConstructionGroupSimpleResponseForDailyReport outsourcingCompanyContractConstructionGroup,
            @Schema(description = "공사항목 목록") List<ConstructionItem> items) {
    }

    /**
     * 외주(공사) 집계 공사항목
     */
    @Schema(description = "외주(공사) 집계 공사항목")
    public record ConstructionItem(
            @Schema(description = "공사항목 ID", example = "1") Long id,
            @Schema(description = "항목", example = "콘크리트 타설") String item,
            @Schema(description = "규격", example = "C24") String specification,
            @Schema(description = "단위", example = "m³") String unit,
            @Schema(description = "도급단가", example = "50000") Long unitPrice,
            @Schema(description = "도급금액 수량", example = "100") Integer contractQuantity,
            @Schema(description = "도급금액 금액", example = "5000000") Long contractPrice,
            @Schema(description = "외주계약금액 수량", example = "100") Integer outsourcingContractQuantity,
            @Schema(description = "외주계약금액 단가", example = "50000") Long outsourcingContractUnitPrice,
            @Schema(description = "외주계약금액 금액", example = "5000000") Long outsourcingContractPrice,
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

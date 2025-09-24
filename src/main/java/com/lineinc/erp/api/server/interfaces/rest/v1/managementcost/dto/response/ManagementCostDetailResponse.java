package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 상세 정보 응답")
public record ManagementCostDetailResponse(
        @Schema(description = "관리비 상세 ID", example = "1") Long id,
        @Schema(description = "품목명", example = "철근") String name,
        @Schema(description = "수량", example = "10") Integer quantity,
        @Schema(description = "단가", example = "10000") Long unitPrice,
        @Schema(description = "공급가", example = "100000") Long supplyPrice,
        @Schema(description = "부가세", example = "10000") Long vat,
        @Schema(description = "합계", example = "110000") Long total,
        @Schema(description = "공제여부", example = "true") Boolean isDeductible,
        @Schema(description = "비고", example = "현장 추가 조정 사항") String memo) {
    public static ManagementCostDetailResponse from(final ManagementCostDetail detail) {
        return new ManagementCostDetailResponse(
                detail.getId(),
                detail.getName(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getSupplyPrice(),
                detail.getVat(),
                detail.getTotal(),
                detail.getIsDeductible(),
                detail.getMemo());
    }
}
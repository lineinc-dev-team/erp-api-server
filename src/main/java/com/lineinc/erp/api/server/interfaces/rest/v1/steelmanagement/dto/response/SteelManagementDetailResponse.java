package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagementDetail;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 관리 상세 정보 응답")
public record SteelManagementDetailResponse(
        @Schema(description = "강재 관리 상세 ID", example = "1") Long id,

        @Schema(description = "규격", example = "D10") String standard,

        @Schema(description = "품명", example = "철근") String name,

        @Schema(description = "단위", example = "EA") String unit,

        @Schema(description = "본", example = "10") Integer count,

        @Schema(description = "길이 (m)", example = "6.0") Double length,

        @Schema(description = "총 길이 (m)", example = "60.0") Double totalLength,

        @Schema(description = "단위중량 (kg/m)", example = "0.56") Double unitWeight,

        @Schema(description = "수량", example = "100") Integer quantity,

        @Schema(description = "단가", example = "12000") Integer unitPrice,

        @Schema(description = "공급가", example = "1200000") Integer supplyPrice,

        @Schema(description = "부가세", example = "120000") Integer vat,

        @Schema(description = "비고", example = "특별 관리 대상") String memo) {
    public static SteelManagementDetailResponse from(final SteelManagementDetail detail) {
        return new SteelManagementDetailResponse(
                detail.getId(),
                detail.getStandard(),
                detail.getName(),
                detail.getUnit(),
                detail.getCount(),
                detail.getLength(),
                detail.getTotalLength(),
                detail.getUnitWeight(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getSupplyPrice(),
                detail.getVat(),
                detail.getMemo());
    }
}

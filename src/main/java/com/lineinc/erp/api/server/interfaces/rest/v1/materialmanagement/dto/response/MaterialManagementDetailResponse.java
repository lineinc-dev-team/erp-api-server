package com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.response;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "자재관리 상세 정보 응답")
public record MaterialManagementDetailResponse(
        @Schema(description = "자재관리 상세 ID", example = "1")
        Long id,

        @Schema(description = "품명", example = "철근 D10")
        String name,

        @Schema(description = "규격", example = "D10")
        String standard,

        @Schema(description = "사용용도", example = "기초공사용")
        String usage,

        @Schema(description = "수량", example = "100")
        Integer quantity,

        @Schema(description = "단가", example = "12000")
        Integer unitPrice,

        @Schema(description = "공급가", example = "1200000")
        Integer supplyPrice,

        @Schema(description = "부가세", example = "120000")
        Integer vat,

        @Schema(description = "합계", example = "1320000")
        Integer total,

        @Schema(description = "비고", example = "특별 관리 대상")
        String memo
) {
    public static MaterialManagementDetailResponse from(MaterialManagementDetail entity) {
        return new MaterialManagementDetailResponse(
                entity.getId(),
                entity.getName(),
                entity.getStandard(),
                entity.getUsage(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSupplyPrice(),
                entity.getVat(),
                entity.getTotal(),
                entity.getMemo()
        );
    }
}

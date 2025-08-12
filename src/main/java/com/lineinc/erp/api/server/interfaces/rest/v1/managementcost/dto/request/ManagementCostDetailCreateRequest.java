package com.lineinc.erp.api.server.presentation.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리비 상세 품목 등록 요청")
public record ManagementCostDetailCreateRequest(
        @NotNull
        @Schema(description = "품목 이름", example = "콘크리트")
        String name,

        @NotNull
        @Schema(description = "단가", example = "1000000")
        Long unitPrice,

        @NotNull
        @Schema(description = "공급가 (부가세 제외 금액)", example = "909090")
        Long supplyPrice,

        @NotNull
        @Schema(description = "부가세", example = "90910")
        Long vat,

        @NotNull
        @Schema(description = "합계 (공급가 + 부가세)", example = "1000000")
        Long total,

        @Schema(description = "비고", example = "첫 번째 기성")
        String memo
) {
}
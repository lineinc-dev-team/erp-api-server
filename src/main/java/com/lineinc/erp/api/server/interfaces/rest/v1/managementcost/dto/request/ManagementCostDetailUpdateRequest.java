package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리비 상세 품목 수정 요청")
public record ManagementCostDetailUpdateRequest(
        @Schema(description = "관리비 상세 품목 ID", example = "1")
        Long id,

        @Schema(description = "품목 이름", example = "콘크리트")
        String name,

        @Schema(description = "단가", example = "1000000")
        Long unitPrice,

        @Schema(description = "공급가 (부가세 제외 금액)", example = "909090")
        Long supplyPrice,

        @Schema(description = "부가세", example = "90910")
        Long vat,

        @Schema(description = "합계 (공급가 + 부가세)", example = "1000000")
        Long total,

        @Schema(description = "비고", example = "첫 번째 기성")
        String memo
) {
}
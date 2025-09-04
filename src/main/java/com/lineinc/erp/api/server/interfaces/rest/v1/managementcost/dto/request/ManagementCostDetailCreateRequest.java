package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리비 상세 품목 등록 요청")
public record ManagementCostDetailCreateRequest(
        @Schema(description = "품목 이름", example = "콘크리트") @NotNull String name,

        @Schema(description = "단가", example = "1000000") @NotNull Long unitPrice,

        @Schema(description = "공급가", example = "909090") @NotNull Long supplyPrice,

        @Schema(description = "부가세", example = "90910") @NotNull Long vat,

        @Schema(description = "합계", example = "1000000") @NotNull Long total,

        @Schema(description = "비고", example = "첫 번째 기성") String memo) {
}
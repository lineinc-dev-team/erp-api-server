package com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "자재관리 상세 수정 요청")
public record MaterialManagementDetailUpdateRequest(
        @Schema(description = "자재관리 상세 ID", example = "1") @NotNull Long id,

        @Schema(description = "품명", example = "철근 D10") @NotNull String name,

        @Schema(description = "규격", example = "10mm") @NotNull String standard,

        @Schema(description = "사용용도", example = "기초 보강") @NotNull String usage,

        @Schema(description = "수량", example = "100") @NotNull Integer quantity,

        @Schema(description = "단가", example = "1200") @NotNull Integer unitPrice,

        @Schema(description = "공급가", example = "120000") @NotNull Integer supplyPrice,

        @Schema(description = "부가세", example = "12000") @NotNull Integer vat,

        @Schema(description = "합계", example = "132000") @NotNull Integer total,

        @Schema(description = "비고", example = "1차 납품분") String memo) {
}

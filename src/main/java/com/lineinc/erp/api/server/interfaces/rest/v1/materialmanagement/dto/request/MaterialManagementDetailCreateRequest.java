package com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "자재관리 상세 등록 요청")
public record MaterialManagementDetailCreateRequest(

        @NotNull @Schema(description = "품명", example = "철근 D10") String name,

        @NotNull @Schema(description = "규격", example = "10mm") String standard,

        @NotNull @Schema(description = "사용용도", example = "기초 보강") String usage,

        @NotNull @Schema(description = "수량", example = "100") Integer quantity,

        @NotNull @Schema(description = "단가", example = "1200") Integer unitPrice,

        @NotNull @Schema(description = "공급가", example = "120000") Integer supplyPrice,

        @NotNull @Schema(description = "부가세", example = "12000") Integer vat,

        @NotNull @Schema(description = "합계", example = "132000") Integer total,

        @Schema(description = "비고", example = "1차 납품분") String memo) {
}

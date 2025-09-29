package com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "현장 계약 등록 요청")
public record CreateSiteContractRequest(
        @NotBlank @Schema(description = "계약명", example = "전기공사 계약") String name,
        @NotNull @Schema(description = "계약금액", example = "15000000") Long amount,
        @Schema(description = "공급가", example = "13636364") Long supplyPrice,
        @Schema(description = "부가세", example = "1363636") Long vat,
        @Schema(description = "매입세", example = "1000000") Long purchaseTax,
        @Min(0) @Max(100) @Schema(description = "계약이행 보증률", example = "5") Long contractPerformanceGuaranteeRate,
        @Min(0) @Max(100) @Schema(description = "하자이행 보증률", example = "3") Long defectPerformanceGuaranteeRate,
        @Min(0) @Max(99) @Schema(description = "하자보증기간", example = "24") Long defectWarrantyPeriod,
        @Schema(description = "비고") String memo,
        @Valid @Schema(description = "계약 관련 파일 목록") List<CreateSiteFileRequest> files) {
}

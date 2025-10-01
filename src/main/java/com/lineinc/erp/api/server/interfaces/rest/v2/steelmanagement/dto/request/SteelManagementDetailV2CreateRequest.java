package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request;

import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "강재수불부 V2 상세 항목 등록 요청")
public record SteelManagementDetailV2CreateRequest(
        @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "타입 (입고/출고/사장/고철)", example = "INCOMING", required = true) @NotNull SteelManagementDetailV2Type type,
        @Schema(description = "품명", example = "철근") @NotNull String name,
        @Schema(description = "규격", example = "D10") @NotNull String specification,
        @Schema(description = "무게 (kg)", example = "5.6") @NotNull Double weight,
        @Schema(description = "본", example = "10") @NotNull Integer count,
        @Schema(description = "총무게 (kg)", example = "56.0") @NotNull Double totalWeight,
        @Schema(description = "단가 (원)", example = "12000") @NotNull Long unitPrice,
        @Schema(description = "금액 (원)", example = "672000") @NotNull Long amount,
        @Schema(description = "구분 (자사자재/구매/임대)", example = "PURCHASE") @NotNull SteelManagementDetailV2Category category,
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/file.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "steel_report.pdf") String originalFileName,
        @Schema(description = "메모", example = "특별 관리 대상") String memo) {
}

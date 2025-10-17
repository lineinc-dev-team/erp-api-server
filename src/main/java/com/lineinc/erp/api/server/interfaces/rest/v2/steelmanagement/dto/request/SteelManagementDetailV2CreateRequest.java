package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request;

import java.time.LocalDate;

import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재수불부 V2 상세 항목 등록 요청")
public record SteelManagementDetailV2CreateRequest(
        @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "타입 (입고/출고/사장/고철)", example = "INCOMING", required = true) SteelManagementDetailV2Type type,
        @Schema(description = "품명", example = "H Beam") String name,
        @Schema(description = "규격", example = "D10") String specification,
        @Schema(description = "무게 (톤)", example = "5.6") Double weight,
        @Schema(description = "본", example = "10") Integer count,
        @Schema(description = "총무게 (톤)", example = "56.0") Double totalWeight,
        @Schema(description = "단가 (원)", example = "12000") Long unitPrice,
        @Schema(description = "공급가 (원)", example = "672000") Long amount,
        @Schema(description = "부가세 (원)", example = "120000") Long vat,
        @Schema(description = "합계 (원)", example = "792000") Long total,
        @Schema(description = "구분 (자사자재/구매/임대)", example = "PURCHASE") SteelManagementDetailV2Category category,
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/file.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "steel_report.pdf") String originalFileName,
        @Schema(description = "입고일", example = "2024-01-15") LocalDate incomingDate,
        @Schema(description = "출고일", example = "2024-01-15") LocalDate outgoingDate,
        @Schema(description = "판매일", example = "2024-01-15") LocalDate salesDate,
        @Schema(description = "메모", example = "특별 관리 대상") String memo) {
}

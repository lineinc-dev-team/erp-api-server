package com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request;

import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재수불부 V2 상세 항목 수정 요청")
public record SteelManagementDetailV2UpdateRequest(
        @Schema(description = "ID (신규는 null)", example = "1") Long id,
        @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "품명", example = "H Beam") String name,
        @Schema(description = "규격", example = "D10") String specification,
        @Schema(description = "무게 (톤)", example = "5.6") Double weight,
        @Schema(description = "본", example = "10") Integer count,
        @Schema(description = "총무게 (톤)", example = "56.0") Double totalWeight,
        @Schema(description = "단가 (원)", example = "12000") Long unitPrice,
        @Schema(description = "금액 (원)", example = "672000") Long amount,
        @Schema(description = "구분 (자사자재/구매/임대)", example = "PURCHASE") SteelManagementDetailV2Category category,
        @Schema(description = "파일 URL", example = "https://s3.amazonaws.com/bucket/file.pdf") String fileUrl,
        @Schema(description = "원본 파일명", example = "steel_report.pdf") String originalFileName,
        @Schema(description = "메모", example = "특별 관리 대상") String memo) {
}

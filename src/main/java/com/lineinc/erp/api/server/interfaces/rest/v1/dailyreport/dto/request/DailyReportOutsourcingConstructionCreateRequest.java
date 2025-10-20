package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주 공사 출역일보 등록 요청")
public record DailyReportOutsourcingConstructionCreateRequest(
        @Schema(description = "업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "외주업체계약 공사항목 ID", example = "1") Long outsourcingCompanyContractConstructionId,
        @Schema(description = "단위", example = "m2") String unit,
        @Schema(description = "수량", example = "100") Integer quantity,
        @Schema(description = "계약서 파일 URL", example = "https://example.com/photo.jpg") String contractFileUrl,
        @Schema(description = "계약서 원본 파일명", example = "photo.jpg") String contractOriginalFileName,
        @Schema(description = "비고", example = "특별 지시사항") String memo) {
}

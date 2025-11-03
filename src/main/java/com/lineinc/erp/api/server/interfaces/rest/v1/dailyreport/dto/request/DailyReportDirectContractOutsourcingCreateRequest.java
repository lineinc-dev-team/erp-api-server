package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "직영/용역 외주업체계약 출역일보 등록 요청")
public record DailyReportDirectContractOutsourcingCreateRequest(
        @Schema(description = "외주업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
        @Schema(description = "외주업체 계약 ID", example = "1") @NotNull Long outsourcingCompanyContractId,
        @Schema(description = "노무인력 ID", example = "1") @NotNull Long laborId,
        @Schema(description = "공수", example = "1.0") @NotNull @Positive Double workQuantity,
        @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
        @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
        @Schema(description = "비고", example = "특별 지시사항") String memo) {
}

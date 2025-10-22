package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "직영/용역 출역일보 등록 요청")
public record DailyReportDirectContractCreateRequest(
        @Schema(description = "업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "노무인력 ID", example = "1") Long laborId,
        @Schema(description = "직급", example = "기사") @NotBlank String position,
        @Schema(description = "작업내용", example = "기초공사") @NotBlank String workContent,
        @Schema(description = "단가", example = "50000") @NotNull @Positive Long unitPrice,
        @Schema(description = "공수", example = "1.0") @NotNull @Positive Double workQuantity,
        @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
        @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
        @Schema(description = "비고", example = "특별 지시사항") String memo,
        @Schema(description = "임시 인력 여부", example = "false") @NotNull Boolean isTemporary,
        @Schema(description = "임시 인력 이름", example = "김철수") String temporaryLaborName) {
}

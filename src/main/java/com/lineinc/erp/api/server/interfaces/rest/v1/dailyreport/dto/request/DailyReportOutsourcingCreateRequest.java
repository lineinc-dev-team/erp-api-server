package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주 출역일보 등록 요청")
public record DailyReportOutsourcingCreateRequest(
        @Schema(description = "업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
        @Schema(description = "외주업체계약 인력 ID", example = "1") @NotNull Long outsourcingCompanyContractWorkerId,
        @Schema(description = "구분값", example = "기초공사") @NotBlank String category,
        @Schema(description = "작업내용", example = "기초공사") @NotBlank String workContent,
        @Schema(description = "공수", example = "8.0") @NotNull Double workQuantity,
        @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
        @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
        @Schema(description = "비고", example = "특별 지시사항") String memo,
        @Schema(description = "파일 목록") @Valid List<DailyReportDocumentCreateRequest> files) {
}

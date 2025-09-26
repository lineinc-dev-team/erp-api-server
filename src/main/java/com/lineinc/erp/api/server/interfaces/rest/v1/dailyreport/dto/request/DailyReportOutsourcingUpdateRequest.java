package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "출역일보 외주 수정 요청")
public record DailyReportOutsourcingUpdateRequest(
        @Schema(description = "수정할 외주 정보 목록") List<@Valid OutsourcingUpdateInfo> outsourcings) {

    @Schema(description = "외주 정보 수정 내용")
    public record OutsourcingUpdateInfo(
            @Schema(description = "ID", example = "1") Long id,
            @Schema(description = "외주업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
            @Schema(description = "외주업체계약 인력 ID", example = "1") @NotNull Long outsourcingCompanyContractWorkerId,
            @Schema(description = "구분값", example = "토목공사") @NotBlank String category,
            @Schema(description = "작업내용", example = "기초 콘크리트 타설") @NotBlank String workContent,
            @Schema(description = "공수", example = "1.0") @NotNull @Positive Double workQuantity,
            @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
            @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
            @Schema(description = "비고", example = "오전 작업") String memo) {
    }
}

package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "출역일보 직영/계약직 수정 요청")
public record DailyReportDirectContractUpdateRequest(
        @Schema(description = "수정할 직영/계약직 정보 목록") List<@Valid DirectContractUpdateInfo> directContracts) {

    @Schema(description = "직영/계약직 정보 수정 내용")
    public record DirectContractUpdateInfo(
            @Schema(description = "ID", example = "1") Long id,
            @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
            @Schema(description = "노무인력 ID", example = "1") Long laborId,
            @Schema(description = "직종", example = "토목공") @NotBlank String position,
            @Schema(description = "작업내용", example = "기초 콘크리트 타설") @NotBlank String workContent,
            @Schema(description = "단가", example = "50000") @NotNull @Positive Long unitPrice,
            @Schema(description = "공수", example = "1.0") @NotNull @Positive Double workQuantity,
            @Schema(description = "사진 URL", example = "https://example.com/photo.jpg") String fileUrl,
            @Schema(description = "사진 원본 파일명", example = "photo.jpg") String originalFileName,
            @Schema(description = "비고", example = "오전 작업") String memo,
            @Schema(description = "임시 인력 여부", example = "false") @NotNull Boolean isTemporary,
            @Schema(description = "임시 인력 이름", example = "김철수") String temporaryLaborName) {
    }
}

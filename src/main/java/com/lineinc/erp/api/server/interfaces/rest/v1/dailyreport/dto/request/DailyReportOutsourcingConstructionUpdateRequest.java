package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "출역일보 외주(공사) 수정 요청")
public record DailyReportOutsourcingConstructionUpdateRequest(
        @Schema(description = "수정할 외주(공사) 그룹 정보 목록") List<@Valid ConstructionGroupUpdateInfo> outsourcingConstructions) {

    @Schema(description = "외주(공사) 그룹 수정 정보")
    public record ConstructionGroupUpdateInfo(
            @Schema(description = "그룹 ID (수정 시 필수, 생성 시 null)", example = "1") Long id,
            @Schema(description = "업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
            @Schema(description = "외주업체계약 공사항목 그룹 ID", example = "1") @NotNull Long outsourcingCompanyContractConstructionGroupId,
            @Schema(description = "공사항목 목록") @Valid List<ConstructionItemUpdateInfo> items) {
    }

    @Schema(description = "공사항목 수정 정보")
    public record ConstructionItemUpdateInfo(
            @Schema(description = "공사항목 ID (수정 시 필수, 생성 시 null)", example = "1") Long id,
            @Schema(description = "외주업체계약 공사항목 ID", example = "1") @NotNull Long outsourcingCompanyContractConstructionId,
            @Schema(description = "규격", example = "C24") String specification,
            @Schema(description = "단위", example = "m2") String unit,
            @Schema(description = "수량", example = "100") Integer quantity,
            @Schema(description = "계약서 파일 URL", example = "https://example.com/photo.jpg") String contractFileUrl,
            @Schema(description = "계약서 원본 파일명", example = "photo.jpg") String contractOriginalFileName,
            @Schema(description = "비고", example = "특별 지시사항") String memo) {
    }
}

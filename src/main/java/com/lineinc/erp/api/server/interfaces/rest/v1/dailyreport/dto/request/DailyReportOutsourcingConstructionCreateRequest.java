package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주 공사 출역일보 등록 요청 (3depth 구조)")
public record DailyReportOutsourcingConstructionCreateRequest(
        @Schema(description = "업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
        @Schema(description = "공사 그룹 목록") @Valid List<ConstructionGroupCreateRequest> groups) {

    @Schema(description = "공사 그룹 정보")
    public record ConstructionGroupCreateRequest(
            @Schema(description = "외주업체계약 공사항목 그룹 ID", example = "1") @NotNull Long outsourcingCompanyContractConstructionGroupId,
            @Schema(description = "공사항목 목록") @Valid List<ConstructionItemCreateRequest> items) {
    }

    @Schema(description = "공사항목 정보")
    public record ConstructionItemCreateRequest(
            @Schema(description = "외주업체계약 공사항목 ID", example = "1") @NotNull Long outsourcingCompanyContractConstructionId,
            @Schema(description = "규격", example = "C24") String specification,
            @Schema(description = "단위", example = "m2") String unit,
            @Schema(description = "수량", example = "100") Integer quantity,
            @Schema(description = "계약서 파일 URL", example = "https://example.com/photo.jpg") String contractFileUrl,
            @Schema(description = "계약서 원본 파일명", example = "photo.jpg") String contractOriginalFileName,
            @Schema(description = "비고", example = "특별 지시사항") String memo) {
    }
}

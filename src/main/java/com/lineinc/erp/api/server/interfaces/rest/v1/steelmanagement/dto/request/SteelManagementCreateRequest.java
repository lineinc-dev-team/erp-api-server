package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

@Schema(description = "강재 관리 등록 요청")
public record SteelManagementCreateRequest(
        @Schema(description = "현장 ID", example = "1") Long siteId,
        @Schema(description = "공정 ID", example = "1") Long siteProcessId,
        @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "용도", example = "철근 콘크리트 타설용") String usage,
        @Schema(description = "강재 수불 구분", example = "ORDER") SteelManagementType type,
        @Schema(description = "기간 시작일", example = "2024-07-01") LocalDate startDate,
        @Schema(description = "기간 종료일", example = "2024-07-31") LocalDate endDate,
        @Schema(description = "비고", example = "2024년 7월 강재 입출고") String memo,
        @Valid @Schema(description = "강재 관리 상세 품목 목록") List<SteelManagementDetailCreateRequest> details,
        @Schema(description = "강재 관리 파일 목록") List<SteelManagementFileCreateRequest> files) {
}

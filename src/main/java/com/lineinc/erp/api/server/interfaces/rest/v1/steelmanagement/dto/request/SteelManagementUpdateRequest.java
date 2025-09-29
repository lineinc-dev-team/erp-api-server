package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 관리 수정 요청")
public record SteelManagementUpdateRequest(
        @Schema(description = "현장 ID", example = "1") Long siteId,
        @Schema(description = "공정 ID", example = "10") Long siteProcessId,
        @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "용도", example = "철근 콘크리트 타설용") String usage,
        @Schema(description = "기간 시작일", example = "2024-07-01") LocalDate startDate,
        @Schema(description = "기간 종료일", example = "2024-07-31") LocalDate endDate,
        @Schema(description = "비고", example = "2024년 7월 강재 입출고") String memo,
        @Schema(description = "강재 관리 상세 품목 목록") List<SteelManagementDetailUpdateRequest> details,
        @Schema(description = "강재 관리 반출 상세 품목 목록") List<SteelManagementReturnDetailUpdateRequest> returnDetails,
        @Schema(description = "강재 관리 파일 목록") List<SteelManagementFileUpdateRequest> files,
        @Schema(description = "수정 이력 리스트") List<SteelManagementUpdateRequest.ChangeHistoryRequest> changeHistories) {
    public record ChangeHistoryRequest(
            @Schema(description = "수정 이력 번호", example = "1") Long id,
            @Schema(description = "변경 사유 또는 비고", example = "변경에 따른 업데이트") String memo) {
    }
}

package com.lineinc.erp.api.server.presentation.v1.steelmanagement.dto.response;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "강재 관리 목록 응답")
public record SteelManagementResponse(
        @Schema(description = "강재 관리 ID", example = "1")
        Long id,

        @Schema(description = "현장명", example = "서울지사 공사현장")
        String siteName,

        @Schema(description = "공정명", example = "철근 배근 작업")
        String processName,

        @Schema(description = "용도", example = "철근 콘크리트 타설용")
        String usage,

        @Schema(description = "구매일자", example = "2024-07-25T00:00:00+09:00")
        OffsetDateTime paymentDate,

        @Schema(description = "첨부파일 존재 여부", example = "true")
        boolean hasFile,

        @Schema(description = "비고", example = "7월 강재 입출고 내역")
        String memo,

        @Schema(description = "강재 상세 품목 목록")
        List<SteelManagementDetailResponse> details


) {
    public static SteelManagementResponse from(SteelManagement entity, List<SteelManagementDetailResponse> details) {
        return new SteelManagementResponse(
                entity.getId(),
                entity.getSite().getName(),
                entity.getSiteProcess().getName(),
                entity.getUsage(),
                entity.getPaymentDate(),
                entity.getFiles() != null && !entity.getFiles().isEmpty(),
                entity.getMemo(),
                details
        );
    }
}

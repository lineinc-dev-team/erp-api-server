package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.site.SiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.siteprocess.SiteProcessResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 관리 상세 응답")
public record SteelManagementDetailViewResponse(
        @Schema(description = "강재 관리 ID", example = "1") Long id,

        @Schema(description = "용도", example = "철근 콘크리트 타설용") String usage,

        @Schema(description = "강재 수불 유형", example = "PURCHASE") String type,

        @Schema(description = "첨부파일 존재 여부", example = "true") boolean hasFile,

        @Schema(description = "비고", example = "7월 강재 입출고 내역") String memo,

        @Schema(description = "강재 상세 품목 목록") List<SteelManagementDetailResponse> details,

        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,

        @Schema(description = "강재 관리 첨부파일 목록") List<SteelManagementFileResponse> files

) {
    public static SteelManagementDetailViewResponse from(SteelManagement entity) {
        List<SteelManagementDetailResponse> details = entity.getDetails().stream()
                .map(SteelManagementDetailResponse::from)
                .toList();

        List<SteelManagementFileResponse> files = entity.getFiles().stream()
                .map(SteelManagementFileResponse::from)
                .toList();

        String typeLabel = entity.getType() != null ? entity.getType().getLabel() : null;

        return new SteelManagementDetailViewResponse(
                entity.getId(),
                entity.getUsage(),
                typeLabel,
                entity.getFiles() != null && !entity.getFiles().isEmpty(),
                entity.getMemo(),
                details,
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()),
                files);
    }
}

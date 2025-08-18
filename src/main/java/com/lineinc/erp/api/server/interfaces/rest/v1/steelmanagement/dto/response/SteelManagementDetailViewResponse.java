package com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.site.SiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.siteprocess.SiteProcessResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "강재 관리 상세 응답")
public record SteelManagementDetailViewResponse(
        @Schema(description = "강재 관리 ID", example = "1") Long id,

        @Schema(description = "용도", example = "철근 콘크리트 타설용") String usage,

        @Schema(description = "강재 수불 유형", example = "발주") String type,

        @Schema(description = "강재 수불 유형 코드", example = "PURCHASE") String typeCode,

        @Schema(description = "이전 강재 수불 유형", example = "발주") String previousType,

        @Schema(description = "이전 강재 수불 유형 코드", example = "ORDER") String previousTypeCode,

        @Schema(description = "기간 시작일", example = "2024-07-01T00:00:00+09:00") OffsetDateTime startDate,

        @Schema(description = "기간 종료일", example = "2024-07-31T00:00:00+09:00") OffsetDateTime endDate,

        @Schema(description = "발주일", example = "2024-07-15T00:00:00+09:00") OffsetDateTime orderDate,

        @Schema(description = "승인일", example = "2024-07-20T00:00:00+09:00") OffsetDateTime approvalDate,

        @Schema(description = "반출일", example = "2024-07-25T00:00:00+09:00") OffsetDateTime releaseDate,

        @Schema(description = "비고", example = "7월 강재 입출고 내역") String memo,

        @Schema(description = "강재 상세 품목 목록") List<SteelManagementDetailResponse> details,

        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,

        @Schema(description = "외주업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,

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
        String typeCode = entity.getType() != null ? entity.getType().name() : null;
        String previousTypeLabel = entity.getPreviousType() != null ? entity.getPreviousType().getLabel() : null;
        String previousTypeCode = entity.getPreviousType() != null ? entity.getPreviousType().name() : null;

        return new SteelManagementDetailViewResponse(
                entity.getId(),
                entity.getUsage(),
                typeLabel,
                typeCode,
                previousTypeLabel,
                previousTypeCode,
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getOrderDate(),
                entity.getApprovalDate(),
                entity.getReleaseDate(),
                entity.getMemo(),
                details,
                SiteResponse.SiteSimpleResponse.from(entity.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(entity.getSiteProcess()),
                entity.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(entity.getOutsourcingCompany())
                        : null,
                files);
    }
}

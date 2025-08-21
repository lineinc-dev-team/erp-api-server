package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.site.SiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.siteprocess.SiteProcessResponse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "관리비 정보 응답")
public record ManagementCostResponse(
        @Schema(description = "관리비 ID", example = "1") Long id,

        @Schema(description = "품목 타입", example = "월세") String itemType,

        @Schema(description = "품목 설명", example = "6월 전기요금") String itemDescription,

        @Schema(description = "결제일", example = "2024-01-01T00:00:00Z") OffsetDateTime paymentDate,

        @Schema(description = "첨부파일 존재 여부", example = "true") Boolean hasFile,

        @Schema(description = "비고", example = "기타 메모") String memo,

        @Schema(description = "관리비 상세 항목 목록") List<ManagementCostDetailResponse> details,

        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process) {

    public static ManagementCostResponse from(ManagementCost cost) {
        return new ManagementCostResponse(
                cost.getId(),
                cost.getItemType().getLabel(),
                cost.getItemDescription(),
                cost.getPaymentDate(),
                cost.getFiles() != null && !cost.getFiles().isEmpty(),
                cost.getMemo(),
                cost.getDetails().stream().map(ManagementCostDetailResponse::from).toList(),
                SiteResponse.SiteSimpleResponse.from(cost.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(cost.getSiteProcess()));
    }
}
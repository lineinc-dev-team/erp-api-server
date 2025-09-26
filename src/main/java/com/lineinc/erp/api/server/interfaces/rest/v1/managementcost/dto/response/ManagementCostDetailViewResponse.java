package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리비 정보 응답")
public record ManagementCostDetailViewResponse(
        @Schema(description = "관리비 ID", example = "1") Long id,

        @Schema(description = "항목 타입", example = "월세") String itemType,

        @Schema(description = "항목 타입 코드", example = "MONTHLY_RENT") String itemTypeCode,

        @Schema(description = "항목 설명", example = "6월 전기요금") String itemDescription,

        @Schema(description = "결제일", example = "2024-01-01T00:00:00Z") OffsetDateTime paymentDate,

        @Schema(description = "비고", example = "기타 메모") String memo,

        @Schema(description = "관리비 상세 항목 목록") List<ManagementCostDetailResponse> details,

        @Schema(description = "첨부파일 목록") List<ManagementCostFileResponse> files,

        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,

        @Schema(description = "외주업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,

        @Schema(description = "전도금 상세 목록") List<ManagementCostKeyMoneyDetailResponse> keyMoneyDetails,

        @Schema(description = "식대 상세 목록") List<ManagementCostMealFeeDetailResponse> mealFeeDetails) {

    public static ManagementCostDetailViewResponse from(final ManagementCost cost) {
        return new ManagementCostDetailViewResponse(
                cost.getId(),
                cost.getItemType().getLabel(),
                cost.getItemType().name(),
                cost.getItemTypeDescription(),
                cost.getPaymentDate(),
                cost.getMemo(),
                cost.getDetails().stream().map(ManagementCostDetailResponse::from)
                        .sorted(Comparator.comparing(ManagementCostDetailResponse::id))
                        .toList(),
                cost.getFiles().stream().map(ManagementCostFileResponse::from)
                        .sorted(Comparator.comparing(ManagementCostFileResponse::id))
                        .toList(),
                SiteResponse.SiteSimpleResponse.from(cost.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(cost.getSiteProcess()),
                cost.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(cost.getOutsourcingCompany())
                        : null,
                cost.getKeyMoneyDetails().stream().map(ManagementCostKeyMoneyDetailResponse::from)
                        .sorted(Comparator.comparing(ManagementCostKeyMoneyDetailResponse::id))
                        .toList(),
                cost.getMealFeeDetails().stream().map(ManagementCostMealFeeDetailResponse::from)
                        .sorted(Comparator.comparing(ManagementCostMealFeeDetailResponse::id))
                        .toList());
    }
}
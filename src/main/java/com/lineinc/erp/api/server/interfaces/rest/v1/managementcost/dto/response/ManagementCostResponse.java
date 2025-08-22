package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.site.SiteResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.siteprocess.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "관리비 정보 응답")
public record ManagementCostResponse(
        @Schema(description = "관리비 ID", example = "1") Long id,

        @Schema(description = "항목 타입", example = "월세") String itemType,

        @Schema(description = "품목 타입 코드", example = "MONTHLY_RENT") String itemTypeCode,

        @Schema(description = "항목 설명", example = "6월 전기요금") String itemTypeDescription,

        @Schema(description = "결제일", example = "2024-01-01T00:00:00Z") OffsetDateTime paymentDate,

        @Schema(description = "첨부파일 존재 여부", example = "true") Boolean hasFile,

        @Schema(description = "비고", example = "기타 메모") String memo,

        @Schema(description = "공급가 총합", example = "1000000") Long supplyPrice,

        @Schema(description = "부가세 총합", example = "100000") Long vat,

        @Schema(description = "합계 총합", example = "1100000") Long total,

        @Schema(description = "현장 요약 정보") SiteResponse.SiteSimpleResponse site,

        @Schema(description = "공정 요약 정보") SiteProcessResponse.SiteProcessSimpleResponse process,

        @Schema(description = "업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany

) {
    public static ManagementCostResponse from(ManagementCost cost) {
        return new ManagementCostResponse(
                cost.getId(),
                cost.getItemType().getLabel(),
                cost.getItemType().name(),
                cost.getItemTypeDescription(),
                cost.getPaymentDate(),
                cost.getFiles() != null && !cost.getFiles().isEmpty(),
                cost.getMemo(),
                cost.getDetails().stream()
                        .filter(detail -> detail.getSupplyPrice() != null)
                        .mapToLong(detail -> detail.getSupplyPrice())
                        .sum(),
                cost.getDetails().stream()
                        .filter(detail -> detail.getVat() != null)
                        .mapToLong(detail -> detail.getVat())
                        .sum(),
                cost.getDetails().stream()
                        .filter(detail -> detail.getTotal() != null)
                        .mapToLong(detail -> detail.getTotal())
                        .sum(),
                SiteResponse.SiteSimpleResponse.from(cost.getSite()),
                SiteProcessResponse.SiteProcessSimpleResponse.from(cost.getSiteProcess()),
                cost.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(cost.getOutsourcingCompany())
                        : null);
    }
}
package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response;

import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 현장별 월별 비용 응답
 */
@Schema(description = "현장별 월별 비용 응답")
public record SiteMonthlyCostResponse(
        @Schema(description = "현장 간단 정보") SiteResponse.SiteSimpleResponse site,
        @Schema(description = "공정 간단 정보") SiteProcessResponse.SiteProcessSimpleResponse siteProcess,
        @Schema(description = "년월 (YYYY-MM)", example = "2025-01") String yearMonth,
        @Schema(description = "재료비 (원)", example = "1000000") Long materialCost,
        @Schema(description = "노무비 (원)", example = "500000") Long laborCost,
        @Schema(description = "관리비 (원)", example = "200000") Long managementCost,
        @Schema(description = "장비비 (원)", example = "300000") Long equipmentCost,
        @Schema(description = "외주비 (원)", example = "400000") Long outsourcingCost,
        @Schema(description = "총 비용 (원)", example = "2400000") Long totalCost) {

    public static SiteMonthlyCostResponse from(
            final Site site,
            final SiteProcess siteProcess,
            final String yearMonth,
            final Long materialCost,
            final Long laborCost,
            final Long managementCost,
            final Long equipmentCost,
            final Long outsourcingCost) {
        final long total = (materialCost != null ? materialCost : 0L)
                + (laborCost != null ? laborCost : 0L)
                + (managementCost != null ? managementCost : 0L)
                + (equipmentCost != null ? equipmentCost : 0L)
                + (outsourcingCost != null ? outsourcingCost : 0L);

        return new SiteMonthlyCostResponse(
                SiteResponse.SiteSimpleResponse.from(site),
                SiteProcessResponse.SiteProcessSimpleResponse.from(siteProcess),
                yearMonth,
                materialCost,
                laborCost,
                managementCost,
                equipmentCost,
                outsourcingCost,
                total);
    }
}

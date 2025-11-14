package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response;

import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 현장별 월별 비용 총합 응답
 */
@Schema(description = "현장별 월별 비용 총합 응답")
public record SiteMonthlyCostsResponse(
        @Schema(description = "현장 간단 정보") SiteResponse.SiteSimpleResponse site,
        @Schema(description = "공정 간단 정보") SiteProcessResponse.SiteProcessSimpleResponse siteProcess,
        @Schema(description = "재료비 총합 (원)", example = "1000000") Long materialCost,
        @Schema(description = "노무비 총합 (원)", example = "500000") Long laborCost,
        @Schema(description = "관리비 총합 (원)", example = "200000") Long managementCost,
        @Schema(description = "장비비 총합 (원)", example = "300000") Long equipmentCost,
        @Schema(description = "외주비 총합 (원)", example = "400000") Long outsourcingCost,
        @Schema(description = "전체 비용 총합 (원)", example = "2400000") Long totalCost) {
}

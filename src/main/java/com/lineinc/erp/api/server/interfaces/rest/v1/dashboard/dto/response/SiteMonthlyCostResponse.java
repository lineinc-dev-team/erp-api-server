package com.lineinc.erp.api.server.interfaces.rest.v1.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 현장별 월별 비용 응답
 */
@Schema(description = "현장별 월별 비용 응답")
public record SiteMonthlyCostResponse(
        @Schema(description = "년월 (YYYY-MM)", example = "2025-01") String yearMonth,
        @Schema(description = "재료비 (원)", example = "1000000") Long materialCost,
        @Schema(description = "노무비 (원)", example = "500000") Long laborCost,
        @Schema(description = "관리비 (원)", example = "200000") Long managementCost,
        @Schema(description = "장비비 (원)", example = "300000") Long equipmentCost,
        @Schema(description = "외주비 (원)", example = "400000") Long outsourcingCost,
        @Schema(description = "총 비용 (원)", example = "2400000") Long totalCost) {

    public static SiteMonthlyCostResponse from(
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
                yearMonth,
                materialCost,
                laborCost,
                managementCost,
                equipmentCost,
                outsourcingCost,
                total);
    }
}

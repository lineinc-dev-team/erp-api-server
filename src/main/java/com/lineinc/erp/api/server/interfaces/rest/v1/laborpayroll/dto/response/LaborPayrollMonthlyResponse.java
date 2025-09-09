package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse.SiteProcessSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse.SiteSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 노무명세서 월별 목록 응답 DTO
 */
@Schema(description = "노무명세서 월별 목록 응답")
public record LaborPayrollMonthlyResponse(
        @Schema(description = "월별 노무명세서 목록") List<LaborPayrollMonthlyItem> items) {

    /**
     * 월별 노무명세서 항목
     */
    @Schema(description = "월별 노무명세서 항목")
    public record LaborPayrollMonthlyItem(
            @Schema(description = "ID") Long id,
            @Schema(description = "현장 정보") SiteSimpleResponse site,
            @Schema(description = "공정 정보") SiteProcessSimpleResponse siteProcess,
            @Schema(description = "조회 년월 (YYYY-MM)") String yearMonth,
            @Schema(description = "정직원 수") Integer regularEmployeeCount,
            @Schema(description = "직영/계약직 수") Integer directContractCount,
            @Schema(description = "기타 수") Integer etcCount,
            @Schema(description = "노무비 합계") BigDecimal totalLaborCost,
            @Schema(description = "공제금 합계") BigDecimal totalDeductions,
            @Schema(description = "차감지급 합계") BigDecimal totalNetPayment,
            @Schema(description = "생성일") OffsetDateTime createdAt,
            @Schema(description = "수정일") OffsetDateTime updatedAt) {
    }
}

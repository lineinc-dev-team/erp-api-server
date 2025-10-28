package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayrollSummary;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse.SiteProcessSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse.SiteSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 노무명세서 집계 응답 DTO
 */
@Schema(description = "노무명세서 집계 응답")
public record LaborPayrollSummaryResponse(
        @Schema(description = "ID") Long id,
        @Schema(description = "현장 정보") SiteSimpleResponse site,
        @Schema(description = "공정 정보") SiteProcessSimpleResponse siteProcess,
        @Schema(description = "조회 년월 (YYYY-MM)") String yearMonth,
        @Schema(description = "정직원 수") Integer regularEmployeeCount,
        @Schema(description = "직영 수") Integer directContractCount,
        @Schema(description = "용역 수") Integer outsourcingCount,
        @Schema(description = "기타 수") Integer etcCount,
        @Schema(description = "노무비 합계") BigDecimal totalLaborCost,
        @Schema(description = "공제금 합계") BigDecimal totalDeductions,
        @Schema(description = "차감지급 합계") BigDecimal totalNetPayment,
        @Schema(description = "비고") String memo,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt) {

    /**
     * LaborPayrollSummary 엔티티로부터 DTO 생성
     */
    public static LaborPayrollSummaryResponse from(final LaborPayrollSummary summary) {
        return new LaborPayrollSummaryResponse(
                summary.getId(),
                SiteSimpleResponse.from(summary.getSite()),
                SiteProcessSimpleResponse.from(summary.getSiteProcess()),
                summary.getYearMonth(),
                summary.getRegularEmployeeCount(),
                summary.getDirectContractCount(),
                summary.getOutsourcingCount(),
                summary.getEtcCount(),
                summary.getTotalLaborCost(),
                summary.getTotalDeductions(),
                summary.getTotalNetPayment(),
                summary.getMemo(),
                summary.getCreatedAt(),
                summary.getUpdatedAt());
    }
}

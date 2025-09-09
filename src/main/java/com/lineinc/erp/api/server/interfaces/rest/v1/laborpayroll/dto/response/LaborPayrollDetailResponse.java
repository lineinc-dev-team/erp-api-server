package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse.SiteProcessSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse.SiteSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 노무명세서 상세 응답 DTO
 */
@Schema(description = "노무명세서 상세 응답")
public record LaborPayrollDetailResponse(
        @Schema(description = "ID") Long id,

        // 인력 정보
        @Schema(description = "노무인력 정보") LaborSimpleResponse labor,

        // 일당 및 근무 정보
        @Schema(description = "일당") Integer dailyWage,
        @Schema(description = "총 근무시간") BigDecimal totalWorkHours,
        @Schema(description = "총 근무일수") BigDecimal totalWorkDays,

        // 1일~31일 공수 정보
        @Schema(description = "1일 공수") Double day01Hours,
        @Schema(description = "2일 공수") Double day02Hours,
        @Schema(description = "3일 공수") Double day03Hours,
        @Schema(description = "4일 공수") Double day04Hours,
        @Schema(description = "5일 공수") Double day05Hours,
        @Schema(description = "6일 공수") Double day06Hours,
        @Schema(description = "7일 공수") Double day07Hours,
        @Schema(description = "8일 공수") Double day08Hours,
        @Schema(description = "9일 공수") Double day09Hours,
        @Schema(description = "10일 공수") Double day10Hours,
        @Schema(description = "11일 공수") Double day11Hours,
        @Schema(description = "12일 공수") Double day12Hours,
        @Schema(description = "13일 공수") Double day13Hours,
        @Schema(description = "14일 공수") Double day14Hours,
        @Schema(description = "15일 공수") Double day15Hours,
        @Schema(description = "16일 공수") Double day16Hours,
        @Schema(description = "17일 공수") Double day17Hours,
        @Schema(description = "18일 공수") Double day18Hours,
        @Schema(description = "19일 공수") Double day19Hours,
        @Schema(description = "20일 공수") Double day20Hours,
        @Schema(description = "21일 공수") Double day21Hours,
        @Schema(description = "22일 공수") Double day22Hours,
        @Schema(description = "23일 공수") Double day23Hours,
        @Schema(description = "24일 공수") Double day24Hours,
        @Schema(description = "25일 공수") Double day25Hours,
        @Schema(description = "26일 공수") Double day26Hours,
        @Schema(description = "27일 공수") Double day27Hours,
        @Schema(description = "28일 공수") Double day28Hours,
        @Schema(description = "29일 공수") Double day29Hours,
        @Schema(description = "30일 공수") Double day30Hours,
        @Schema(description = "31일 공수") Double day31Hours,

        // 노무비 및 공제 정보
        @Schema(description = "총 노무비") BigDecimal totalLaborCost,
        @Schema(description = "국민연금") BigDecimal nationalPension,
        @Schema(description = "건강보험료") BigDecimal healthInsurance,
        @Schema(description = "장기요양보험료") BigDecimal longTermCareInsurance,
        @Schema(description = "고용보험료") BigDecimal employmentInsurance,
        @Schema(description = "소득세") BigDecimal incomeTax,
        @Schema(description = "주민세") BigDecimal localTax,
        @Schema(description = "공제금 합계") BigDecimal totalDeductions,
        @Schema(description = "차감지급 합계") BigDecimal netPayment,

        @Schema(description = "비고") String memo,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt) {

    /**
     * LaborPayroll 엔티티로부터 DTO 생성
     */
    public static LaborPayrollDetailResponse from(LaborPayroll laborPayroll) {
        return new LaborPayrollDetailResponse(
                laborPayroll.getId(),

                // 노무인력 정보
                laborPayroll.getLabor() != null ? LaborSimpleResponse.from(laborPayroll.getLabor()) : null,

                // 일당 및 근무 정보
                laborPayroll.getDailyWage(),
                laborPayroll.getTotalWorkHours(),
                laborPayroll.getTotalWorkDays(),

                // 1일~31일 공수 정보
                laborPayroll.getDay01Hours(),
                laborPayroll.getDay02Hours(),
                laborPayroll.getDay03Hours(),
                laborPayroll.getDay04Hours(),
                laborPayroll.getDay05Hours(),
                laborPayroll.getDay06Hours(),
                laborPayroll.getDay07Hours(),
                laborPayroll.getDay08Hours(),
                laborPayroll.getDay09Hours(),
                laborPayroll.getDay10Hours(),
                laborPayroll.getDay11Hours(),
                laborPayroll.getDay12Hours(),
                laborPayroll.getDay13Hours(),
                laborPayroll.getDay14Hours(),
                laborPayroll.getDay15Hours(),
                laborPayroll.getDay16Hours(),
                laborPayroll.getDay17Hours(),
                laborPayroll.getDay18Hours(),
                laborPayroll.getDay19Hours(),
                laborPayroll.getDay20Hours(),
                laborPayroll.getDay21Hours(),
                laborPayroll.getDay22Hours(),
                laborPayroll.getDay23Hours(),
                laborPayroll.getDay24Hours(),
                laborPayroll.getDay25Hours(),
                laborPayroll.getDay26Hours(),
                laborPayroll.getDay27Hours(),
                laborPayroll.getDay28Hours(),
                laborPayroll.getDay29Hours(),
                laborPayroll.getDay30Hours(),
                laborPayroll.getDay31Hours(),

                // 노무비 및 공제 정보
                laborPayroll.getTotalLaborCost(),
                laborPayroll.getNationalPension(),
                laborPayroll.getHealthInsurance(),
                laborPayroll.getLongTermCareInsurance(),
                laborPayroll.getEmploymentInsurance(),
                laborPayroll.getIncomeTax(),
                laborPayroll.getLocalTax(),
                laborPayroll.getTotalDeductions(),
                laborPayroll.getNetPayment(),

                laborPayroll.getMemo(),
                laborPayroll.getCreatedAt(),
                laborPayroll.getUpdatedAt());
    }
}

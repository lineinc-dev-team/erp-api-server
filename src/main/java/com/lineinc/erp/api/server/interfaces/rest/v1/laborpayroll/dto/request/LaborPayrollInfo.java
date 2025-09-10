package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * 노무명세서 개별 인력 정보 DTO
 */
@Schema(description = "노무명세서 개별 인력 정보")
@Builder
public record LaborPayrollInfo(
        @Schema(description = "노무명세서 ID", example = "1") Long id,

        // 1일~31일까지의 공수 (근무시간)
        @Schema(description = "1일 공수", example = "8.0") Double day01Hours,
        @Schema(description = "2일 공수", example = "8.0") Double day02Hours,
        @Schema(description = "3일 공수", example = "8.0") Double day03Hours,
        @Schema(description = "4일 공수", example = "8.0") Double day04Hours,
        @Schema(description = "5일 공수", example = "8.0") Double day05Hours,
        @Schema(description = "6일 공수", example = "8.0") Double day06Hours,
        @Schema(description = "7일 공수", example = "8.0") Double day07Hours,
        @Schema(description = "8일 공수", example = "8.0") Double day08Hours,
        @Schema(description = "9일 공수", example = "8.0") Double day09Hours,
        @Schema(description = "10일 공수", example = "8.0") Double day10Hours,
        @Schema(description = "11일 공수", example = "8.0") Double day11Hours,
        @Schema(description = "12일 공수", example = "8.0") Double day12Hours,
        @Schema(description = "13일 공수", example = "8.0") Double day13Hours,
        @Schema(description = "14일 공수", example = "8.0") Double day14Hours,
        @Schema(description = "15일 공수", example = "8.0") Double day15Hours,
        @Schema(description = "16일 공수", example = "8.0") Double day16Hours,
        @Schema(description = "17일 공수", example = "8.0") Double day17Hours,
        @Schema(description = "18일 공수", example = "8.0") Double day18Hours,
        @Schema(description = "19일 공수", example = "8.0") Double day19Hours,
        @Schema(description = "20일 공수", example = "8.0") Double day20Hours,
        @Schema(description = "21일 공수", example = "8.0") Double day21Hours,
        @Schema(description = "22일 공수", example = "8.0") Double day22Hours,
        @Schema(description = "23일 공수", example = "8.0") Double day23Hours,
        @Schema(description = "24일 공수", example = "8.0") Double day24Hours,
        @Schema(description = "25일 공수", example = "8.0") Double day25Hours,
        @Schema(description = "26일 공수", example = "8.0") Double day26Hours,
        @Schema(description = "27일 공수", example = "8.0") Double day27Hours,
        @Schema(description = "28일 공수", example = "8.0") Double day28Hours,
        @Schema(description = "29일 공수", example = "8.0") Double day29Hours,
        @Schema(description = "30일 공수", example = "8.0") Double day30Hours,
        @Schema(description = "31일 공수", example = "8.0") Double day31Hours,

        // 일당 관련
        @Schema(description = "일당", example = "150000") BigDecimal dailyWage,

        // 계산된 값들
        @Schema(description = "총 근무시간", example = "176.0") BigDecimal totalWorkHours,
        @Schema(description = "총 근무일수", example = "22.0") BigDecimal totalWorkDays,
        @Schema(description = "총 노무비", example = "3300000") BigDecimal totalLaborCost,

        // 공제 항목들
        @Schema(description = "소득세", example = "15000") BigDecimal incomeTax,
        @Schema(description = "고용보험료", example = "5000") BigDecimal employmentInsurance,
        @Schema(description = "건강보험료", example = "8000") BigDecimal healthInsurance,
        @Schema(description = "주민세", example = "1500") BigDecimal localTax,
        @Schema(description = "국민연금", example = "12000") BigDecimal nationalPension,
        @Schema(description = "장기요양보험료", example = "2000") BigDecimal longTermCareInsurance,
        @Schema(description = "총 공제액", example = "40500") BigDecimal totalDeductions,
        @Schema(description = "차감지급액", example = "3259500") BigDecimal netPayment,
        @Schema(description = "비고", example = "해당 직원 특이사항") String memo) {
}

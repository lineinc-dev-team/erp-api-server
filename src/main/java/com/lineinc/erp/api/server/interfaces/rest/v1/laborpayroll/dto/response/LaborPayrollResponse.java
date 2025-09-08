package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 노무명세서 응답 DTO
 */
public record LaborPayrollResponse(
        Long laborId, // 노무인력 ID
        Long dailyReportId, // 출역일보 ID
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate reportDate, // 출역 날짜
        BigDecimal workHours, // 근무 시간
        BigDecimal workDays, // 근무 일수
        BigDecimal hourlyWage, // 시급
        BigDecimal totalLaborCost, // 노무비 총액 (세전)
        BigDecimal incomeTax, // 소득세
        BigDecimal employmentInsurance, // 고용보험료
        BigDecimal healthInsurance, // 건강보험료
        BigDecimal localTax, // 주민세
        BigDecimal nationalPension, // 국민연금
        BigDecimal longTermCareInsurance, // 장기요양보험료
        BigDecimal totalDeductions, // 총 공제액
        BigDecimal netPayment, // 차감지급액 (실수령액)
        String memo // 비고
) {
}

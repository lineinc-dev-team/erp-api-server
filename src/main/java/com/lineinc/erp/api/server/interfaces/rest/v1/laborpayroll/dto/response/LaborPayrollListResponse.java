package com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse.SiteProcessSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse.SiteSimpleResponse;

/**
 * 노무명세서 응답 DTO (현장별, 공정별, 월별)
 */
public record LaborPayrollListResponse(
        SiteSimpleResponse site, // 현장 정보
        SiteProcessSimpleResponse siteProcess, // 공정 정보
        LocalDate yearMonth, // 조회 년월 (해당월 1일)
        List<LaborPayrollDetailResponse> laborDetails, // 인력별 상세 내역
        LaborPayrollSummaryResponse summary // 월별 합계
) {

    /**
     * 인력별 일별 근무 내역
     */
    public record LaborPayrollDetailResponse(
            Long laborId, // 인력 ID
            String laborName, // 인력명
            String laborType, // 인력 타입
            BigDecimal hourlyWage, // 시급
            Map<Integer, BigDecimal> dailyWorkHours, // 일별 근무시간 (일자 -> 근무시간)
            BigDecimal totalWorkHours, // 총 근무시간
            BigDecimal totalWorkDays, // 총 근무일수
            BigDecimal totalLaborCost, // 총 노무비
            BigDecimal totalDeductions, // 총 공제액
            BigDecimal netPayment // 차감지급액
    ) {
    }

    /**
     * 월별 합계 정보
     */
    public record LaborPayrollSummaryResponse(
            int totalLaborCount, // 총 인력수
            int regularEmployeeCount, // 정직원 수
            int directContractCount, // 직영/계약직 수
            int etcCount, // 기타 수
            BigDecimal totalWorkHours, // 총 근무시간
            BigDecimal totalWorkDays, // 총 근무일수
            BigDecimal totalLaborCost, // 총 노무비
            BigDecimal totalIncomeTax, // 총 소득세
            BigDecimal totalEmploymentInsurance, // 총 고용보험료
            BigDecimal totalHealthInsurance, // 총 건강보험료
            BigDecimal totalLocalTax, // 총 주민세
            BigDecimal totalNationalPension, // 총 국민연금
            BigDecimal totalLongTermCareInsurance, // 총 장기요양보험료
            BigDecimal totalDeductions, // 총 공제액
            BigDecimal totalNetPayment // 총 차감지급액
    ) {
    }
}

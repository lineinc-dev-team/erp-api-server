package com.lineinc.erp.api.server.domain.labormanagement.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

/**
 * 퇴직금 발생 여부 및 누적 근무일수 계산 유틸리티
 */
public class LaborWorkDaysCalculator {

    private static DailyReportRepository dailyReportRepository;

    public static void setDailyReportRepository(DailyReportRepository repository) {
        dailyReportRepository = repository;
    }

    /**
     * 누적 근무일수 계산 (계약직만 대상)
     */
    public static int calculateAccumulatedWorkDays(Labor labor) {
        if (labor.getType() != LaborType.DIRECT_CONTRACT || labor.getHireDate() == null) {
            return 0;
        }

        LocalDate hireDate = labor.getHireDate().toLocalDate();
        LocalDate endDate = labor.getResignationDate() != null
                ? labor.getResignationDate().toLocalDate()
                : LocalDate.now();

        List<DailyReport> dailyReports = dailyReportRepository.findByLaborIdAndDateRange(
                labor.getId(),
                DateTimeFormatUtils.toOffsetDateTime(hireDate),
                DateTimeFormatUtils.toOffsetDateTime(endDate));

        // 실제 출근일수 합산
        int totalWorkDays = 0;

        for (DailyReport dailyReport : dailyReports) {
            for (DailyReportDirectContract directContract : dailyReport.getDirectContracts()) {
                if (directContract.getLabor() != null &&
                        directContract.getLabor().getId().equals(labor.getId())) {

                    int workQty = directContract.getWorkQuantity() != null
                            ? directContract.getWorkQuantity().intValue()
                            : 0;
                    totalWorkDays += (workQty > 0 ? 1 : 0); // 출근한 날수
                }
            }
        }

        return totalWorkDays;
    }

    /**
     * 퇴직금 지급 가능 여부 계산
     * 조건: 근속 1년 이상 && 주 평균 15시간 이상
     */
    public static boolean isEligibleForSeverance(Labor labor) {
        int accumulatedWorkDays = calculateAccumulatedWorkDays(labor);

        // 근속기간(일수)
        long serviceDays = ChronoUnit.DAYS.between(
                labor.getHireDate().toLocalDate(),
                labor.getResignationDate() != null
                        ? labor.getResignationDate().toLocalDate()
                        : LocalDate.now());

        if (serviceDays < 365) {
            return false; // 1년 미만 근속
        }

        // 총 근로시간 / 총 주수 계산
        int totalWorkHours = accumulatedWorkDays * 8; // 출근 1일 = 8시간 가정
        double totalWeeks = serviceDays / 7.0;
        double avgWeeklyHours = totalWorkHours / totalWeeks;

        return avgWeeklyHours >= 15;
    }
}
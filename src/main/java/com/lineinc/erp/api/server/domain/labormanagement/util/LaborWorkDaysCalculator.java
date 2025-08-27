package com.lineinc.erp.api.server.domain.labormanagement.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReportDirectContract;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

/**
 * 노무 인력의 누적 근무일수 계산을 위한 유틸리티 클래스
 */
public class LaborWorkDaysCalculator {

    private static DailyReportRepository dailyReportRepository;

    /**
     * DailyReportRepository 의존성 주입을 위한 메서드
     */
    public static void setDailyReportRepository(DailyReportRepository repository) {
        dailyReportRepository = repository;
    }

    /**
     * 누적 근무일수를 계산합니다.
     * 직영/계약직 인력만 계산 대상입니다. (정직원은 해당 없음)
     * 
     * 비즈니스 로직:
     * - 출역일보 기준으로 실제 근무한 일수만 누적
     * - 주 15시간 이상 근무해야 누적
     * - 월 15-30일 근무 시 해당 월 전체 근무일수 포함
     * 
     * @param labor 대상 노무 인력
     * @return 누적 근무일수
     */
    public static Integer calculateAccumulatedWorkDays(Labor labor) {

        // 정직원이거나 입사일이 없으면 0
        if (labor.getType() == null ||
                labor.getType() == LaborType.REGULAR_EMPLOYEE ||
                labor.getHireDate() == null) {
            return 0;
        }

        // 출역일보 데이터가 있으면 실제 데이터 기반으로 계산
        if (dailyReportRepository != null) {
            return calculateFromDailyReports(labor);
        }

        // 출역일보 데이터가 없으면 보수적으로 계산
        return calculateFallback(labor);
    }

    /**
     * 출역일보 데이터를 기반으로 누적 근무일수를 계산합니다.
     */
    private static Integer calculateFromDailyReports(Labor labor) {
        LocalDate hireDate = labor.getHireDate().toLocalDate();
        LocalDate endDate = LocalDate.now();

        // 입사일부터 현재까지의 출역일보 조회
        List<DailyReport> dailyReports = dailyReportRepository.findByLaborIdAndDateRange(
                labor.getId(),
                DateTimeFormatUtils.toOffsetDateTime(hireDate),
                DateTimeFormatUtils.toOffsetDateTime(endDate));

        if (dailyReports.isEmpty()) {
            return 0;
        }

        // 주별 근무일수 계산 (주 15일 이상 근무 조건 확인)
        Map<Integer, Integer> weeklyWorkDays = calculateWeeklyWorkDays(dailyReports, labor.getId());

        // 월별 근무일수 계산
        Map<Integer, Long> monthlyWorkDays = dailyReports.stream()
                .collect(Collectors.groupingBy(
                        dr -> dr.getReportDate().getYear() * 100 + dr.getReportDate().getMonthValue(),
                        Collectors.counting()));

        int totalWorkDays = 0;

        for (Map.Entry<Integer, Long> entry : monthlyWorkDays.entrySet()) {
            int monthKey = entry.getKey();
            int year = monthKey / 100;
            int month = monthKey % 100;
            long workDaysInMonth = entry.getValue();

            // 해당 월의 주별 근무일수 확인
            boolean hasQualifiedWeeks = hasQualifiedWeeksInMonth(weeklyWorkDays, year, month);

            // 월 15-30일 근무 시 해당 월 전체 근무일수 포함 (단, 주 15일 이상 근무 조건 충족 시)
            if (workDaysInMonth >= 15 && workDaysInMonth <= 30 && hasQualifiedWeeks) {
                // 해당 월의 총 일수 계산 (윤년 고려)
                int daysInMonth = getDaysInMonth(year, month);
                totalWorkDays += daysInMonth;
            } else if (workDaysInMonth > 0) {
                // 15일 미만이면 실제 근무일수만 추가
                totalWorkDays += workDaysInMonth;
            }
        }

        return totalWorkDays;
    }

    /**
     * 주별 근무일수를 계산합니다.
     */
    private static Map<Integer, Integer> calculateWeeklyWorkDays(List<DailyReport> dailyReports, Long laborId) {
        Map<Integer, Integer> weeklyDays = new java.util.HashMap<>();

        for (DailyReport dailyReport : dailyReports) {
            // 직영/계약직 출역일보에서 근무일수 확인 (workQuantity를 근무일수로 사용)
            for (DailyReportDirectContract directContract : dailyReport.getDirectContracts()) {
                if (directContract.getLabor() != null && directContract.getLabor().getId().equals(laborId)) {
                    addWeeklyDays(weeklyDays, dailyReport.getReportDate(),
                            directContract.getWorkQuantity() != null ? directContract.getWorkQuantity().intValue() : 0);
                }
            }
        }

        return weeklyDays;
    }

    /**
     * 특정 날짜의 근무일수를 해당 주에 추가합니다.
     */
    private static void addWeeklyDays(Map<Integer, Integer> weeklyDays, OffsetDateTime reportDate, Integer workDays) {
        if (workDays == null || workDays <= 0) {
            return;
        }

        // 주차 계산 (ISO-8601 기준)
        int weekOfYear = reportDate.toLocalDate().get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int year = reportDate.getYear();
        int weekKey = year * 100 + weekOfYear;

        weeklyDays.merge(weekKey, workDays, Integer::sum);
    }

    /**
     * 특정 월에 주 15일 이상 근무한 주가 있는지 확인합니다.
     */
    private static boolean hasQualifiedWeeksInMonth(Map<Integer, Integer> weeklyWorkDays, int year, int month) {
        for (Map.Entry<Integer, Integer> entry : weeklyWorkDays.entrySet()) {
            int weekKey = entry.getKey();
            int weekYear = weekKey / 100;
            int weekOfYear = weekKey % 100;

            // 해당 년도의 주차가 해당 월에 포함되는지 확인
            if (weekYear == year && isWeekInMonth(weekOfYear, year, month)) {
                if (entry.getValue() >= 15) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 특정 주차가 해당 월에 포함되는지 확인합니다.
     */
    private static boolean isWeekInMonth(int weekOfYear, int year, int month) {
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        int firstWeekOfMonth = firstDayOfMonth.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int lastWeekOfMonth = lastDayOfMonth.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        return weekOfYear >= firstWeekOfMonth && weekOfYear <= lastWeekOfMonth;
    }

    /**
     * 출역일보 데이터가 없을 때의 보수적 계산
     */
    private static Integer calculateFallback(Labor labor) {
        long monthsSinceHire = ChronoUnit.MONTHS.between(
                labor.getHireDate().toLocalDate(),
                LocalDate.now());

        if (monthsSinceHire <= 0) {
            return 0;
        }

        // 보수적인 계산: 월 18일로 추정
        int estimatedDaysPerMonth = 18;
        return (int) (monthsSinceHire * estimatedDaysPerMonth);
    }

    /**
     * 특정 년월의 일수를 계산합니다 (윤년 고려)
     */
    private static int getDaysInMonth(int year, int month) {
        return LocalDate.of(year, month, 1).lengthOfMonth();
    }

    /**
     * 퇴직금 발생 여부를 계산합니다.
     * 누적 근무일수가 180일(6개월) 이상이면 true
     * 
     * @param accumulatedWorkDays 누적 근무일수
     * @return 퇴직금 발생 여부
     */
    public static Boolean calculateSeverancePayEligibility(Integer accumulatedWorkDays) {
        return accumulatedWorkDays >= 180;
    }
}

package com.lineinc.erp.api.server.domain.laborpayroll.service.v1;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.laborpayroll.entity.LaborPayroll;

/**
 * 노무비 계산 로직을 담당하는 서비스 클래스
 * 엔티티에서 복잡한 계산 로직을 분리
 */
public class LaborPayrollCalculator {

    /**
     * 총 근무시간 계산
     */
    public static BigDecimal calculateTotalWorkHours(final LaborPayroll payroll) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 1; i <= 31; i++) {
            final Double dayHours = payroll.getDayHours(i);
            if (dayHours != null && dayHours > 0.0) {
                total = total.add(BigDecimal.valueOf(dayHours));
            }
        }
        return total;
    }

    /**
     * 총 근무일수 계산 (근무시간이 있으면 1일로 계산)
     */
    public static BigDecimal calculateTotalWorkDays(final LaborPayroll payroll) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 1; i <= 31; i++) {
            final Double dayHours = payroll.getDayHours(i);
            if (dayHours != null && dayHours > 0.0) {
                total = total.add(BigDecimal.ONE);
            }
        }
        return total;
    }

    /**
     * 총 노무비 계산 (공수 × 일당 기준)
     */
    public static BigDecimal calculateTotalLaborCost(final Integer dailyWage, final BigDecimal totalWorkHours) {
        if (dailyWage != null && totalWorkHours != null) {
            return BigDecimal.valueOf(dailyWage).multiply(totalWorkHours);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 생년월일 계산 (주민번호 기준)
     */
    public static LocalDate calculateBirthDate(final Labor labor) {
        if (labor == null || labor.getResidentNumber() == null || labor.getResidentNumber().length() < 13) {
            return LocalDate.of(1989, 1, 1); // 기본값
        }

        try {
            final String residentNumber = labor.getResidentNumber().replace("-", "");
            if (residentNumber.length() < 13) {
                return LocalDate.of(1989, 1, 1); // 기본값
            }

            // 생년월일 추출 (처음 6자리)
            final String yymmdd = residentNumber.substring(0, 6);
            final int yy = Integer.parseInt(yymmdd.substring(0, 2));
            final int mm = Integer.parseInt(yymmdd.substring(2, 4));
            final int dd = Integer.parseInt(yymmdd.substring(4, 6));

            // 유효하지 않은 월/일 체크
            if (mm < 1 || mm > 12 || dd < 1 || dd > 31) {
                return LocalDate.of(1989, 1, 1); // 기본값
            }

            // 성별코드 (8번째 자리, 인덱스 7)
            final String genderCode = residentNumber.substring(7, 8);

            // 엑셀 수식과 동일한 로직: 1,2,5,6이면 1900년대, 아니면 2000년대
            int fullYear;
            if ("1".equals(genderCode) || "2".equals(genderCode) ||
                    "5".equals(genderCode) || "6".equals(genderCode)) {
                fullYear = 1900 + yy;
            } else {
                fullYear = 2000 + yy;
            }

            return LocalDate.of(fullYear, mm, dd);

        } catch (final Exception e) {
            // 주민번호 파싱 오류 시 기본값 반환
            return LocalDate.of(1989, 1, 1);
        }
    }

    /**
     * 만나이 계산 (엑셀 수식과 동일: INT((TODAY()-생년월일)/365.25))
     */
    public static int calculateAge(final Labor labor) {
        final LocalDate birthDate = calculateBirthDate(labor);
        final LocalDate today = LocalDate.now();

        // 엑셀 수식과 동일한 방식으로 나이 계산: INT((TODAY()-생년월일)/365.25)
        final long daysDifference = ChronoUnit.DAYS.between(birthDate, today);
        return (int) (daysDifference / 365.25);
    }

    /**
     * 해당 월의 첫 번째 평일 근무 여부 확인
     * 1일이 주말이면 다음 평일을 기준으로 판단
     */
    public static boolean workedOnFirstWeekday(final LaborPayroll payroll, final String yearMonth) {
        // 년월 파싱
        final String[] parts = yearMonth.split("-");
        final int year = Integer.parseInt(parts[0]);
        final int month = Integer.parseInt(parts[1]);

        // 해당 월의 첫 번째 평일 찾기
        final LocalDate firstWeekday = findFirstWeekdayOfMonth(year, month);
        final int firstWeekdayDay = firstWeekday.getDayOfMonth();

        // 첫 번째 평일에 근무했는지 확인
        final Double dayHours = payroll.getDayHours(firstWeekdayDay);
        return dayHours != null && dayHours > 0.0;
    }

    /**
     * 해당 월의 첫 번째 평일 찾기
     */
    private static LocalDate findFirstWeekdayOfMonth(final int year, final int month) {
        final LocalDate firstDay = LocalDate.of(year, month, 1);

        // 1일부터 시작해서 첫 번째 평일(월~금) 찾기
        LocalDate currentDay = firstDay;
        while (currentDay.getDayOfWeek() == DayOfWeek.SATURDAY ||
                currentDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            currentDay = currentDay.plusDays(1);
        }

        return currentDay;
    }

    /**
     * 소득세 계산 (엑셀 수식과 동일)
     * 첫 번째 평일 근무 여부를 기준으로 계산
     */
    public static BigDecimal calculateIncomeTax(final Integer dailyWage, final LaborPayroll payroll,
            final String yearMonth) {
        if (dailyWage == null || !workedOnFirstWeekday(payroll, yearMonth)) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalTax = BigDecimal.ZERO;
        final BigDecimal threshold = new BigDecimal("150000");

        // 각 일별로 소득세 계산
        for (int day = 1; day <= 31; day++) {
            final Double dayHours = payroll.getDayHours(day);
            if (dayHours != null && dayHours > 0.0) {
                // 일당 × 해당일 공수
                final BigDecimal dailyAmount = BigDecimal.valueOf(dailyWage).multiply(BigDecimal.valueOf(dayHours));

                if (dailyAmount.compareTo(threshold) > 0) {
                    // (일당×공수 - 150000) * 6% * 45%
                    final BigDecimal exceededAmount = dailyAmount.subtract(threshold);
                    final BigDecimal dailyTax = exceededAmount
                            .multiply(new BigDecimal("0.06"))
                            .multiply(new BigDecimal("0.45"));
                    totalTax = totalTax.add(roundDown(dailyTax, 0));
                }
            }
        }

        // ROUNDDOWN(SUM, -1) 적용
        return roundDown(totalTax, -1);
    }

    /**
     * ROUNDDOWN 함수 구현
     */
    public static BigDecimal roundDown(final BigDecimal value, final int digits) {
        if (digits >= 0) {
            return value.setScale(digits, java.math.RoundingMode.DOWN);
        } else {
            final BigDecimal divisor = BigDecimal.TEN.pow(-digits);
            return value.divide(divisor, 0, java.math.RoundingMode.DOWN).multiply(divisor);
        }
    }

}

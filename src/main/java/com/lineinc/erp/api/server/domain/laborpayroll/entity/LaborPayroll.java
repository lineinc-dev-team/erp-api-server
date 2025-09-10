package com.lineinc.erp.api.server.domain.laborpayroll.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 노무비 명세서 엔티티
 * 월별 인력별 근무 내역 및 급여 정보를 저장
 */
@Entity
@Table(indexes = {
        @Index(columnList = "year_month"),
        @Index(columnList = "labor_id, year_month"),
        @Index(columnList = "created_at"),
        @Index(columnList = "updated_at")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class LaborPayroll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "labor_payroll_seq")
    @SequenceGenerator(name = "labor_payroll_seq", sequenceName = "labor_payroll_seq", allocationSize = 1)
    private Long id;

    // 인력 정보
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    private Labor labor;

    // 현장 정보
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    // 공정 정보
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id")
    private SiteProcess siteProcess;

    // 기본 정보
    @Column
    private String yearMonth; // 해당 년월 (YYYY-MM 형식)

    // 일당
    @Column
    @DiffInclude
    private Integer dailyWage;

    // 1일~31일까지의 공수 (근무시간)
    @Column
    @DiffInclude
    private Double day01Hours;

    @Column
    @DiffInclude
    private Double day02Hours;

    @Column
    @DiffInclude
    private Double day03Hours;

    @Column
    @DiffInclude
    private Double day04Hours;

    @Column
    @DiffInclude
    private Double day05Hours;

    @Column
    @DiffInclude
    private Double day06Hours;

    @Column
    @DiffInclude
    private Double day07Hours;

    @Column
    @DiffInclude
    private Double day08Hours;

    @Column
    @DiffInclude
    private Double day09Hours;

    @Column
    @DiffInclude
    private Double day10Hours;

    @Column
    @DiffInclude
    private Double day11Hours;

    @Column
    @DiffInclude
    private Double day12Hours;

    @Column
    @DiffInclude
    private Double day13Hours;

    @Column
    @DiffInclude
    private Double day14Hours;

    @Column
    @DiffInclude
    private Double day15Hours;

    @Column
    @DiffInclude
    private Double day16Hours;

    @Column
    @DiffInclude
    private Double day17Hours;

    @Column
    @DiffInclude
    private Double day18Hours;

    @Column
    @DiffInclude
    private Double day19Hours;

    @Column
    @DiffInclude
    private Double day20Hours;

    @Column
    @DiffInclude
    private Double day21Hours;

    @Column
    @DiffInclude
    private Double day22Hours;

    @Column
    @DiffInclude
    private Double day23Hours;

    @Column
    @DiffInclude
    private Double day24Hours;

    @Column
    @DiffInclude
    private Double day25Hours;

    @Column
    @DiffInclude
    private Double day26Hours;

    @Column
    @DiffInclude
    private Double day27Hours;

    @Column
    @DiffInclude
    private Double day28Hours;

    @Column
    @DiffInclude
    private Double day29Hours;

    @Column
    @DiffInclude
    private Double day30Hours;

    @Column
    @DiffInclude
    private Double day31Hours;

    // 계산된 값들
    @Column(precision = 8, scale = 2)
    @DiffInclude
    private BigDecimal totalWorkHours; // 총 근무시간

    @Column(precision = 6, scale = 2)
    @DiffInclude
    private BigDecimal totalWorkDays; // 총 근무일수

    @Column(precision = 12, scale = 2)
    @DiffInclude
    private BigDecimal totalLaborCost; // 총 노무비

    // 공제 항목들
    @Column(precision = 10, scale = 2)
    @DiffInclude
    private BigDecimal incomeTax; // 소득세

    @Column(precision = 10, scale = 2)
    @DiffInclude
    private BigDecimal employmentInsurance; // 고용보험료

    @Column(precision = 10, scale = 2)
    @DiffInclude
    private BigDecimal healthInsurance; // 건강보험료

    @Column(precision = 10, scale = 2)
    @DiffInclude
    private BigDecimal localTax; // 주민세

    @Column(precision = 10, scale = 2)
    @DiffInclude
    private BigDecimal nationalPension; // 국민연금

    @Column(precision = 10, scale = 2)
    @DiffInclude
    private BigDecimal longTermCareInsurance; // 장기요양보험료

    @Column(precision = 12, scale = 2)
    @DiffInclude
    private BigDecimal totalDeductions; // 총 공제액

    @Column(precision = 12, scale = 2)
    @DiffInclude
    private BigDecimal netPayment; // 차감지급액

    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo; // 비고

    /**
     * 특정 일의 근무시간을 가져오는 메서드
     */
    public Double getDayHours(int day) {
        return switch (day) {
            case 1 -> day01Hours;
            case 2 -> day02Hours;
            case 3 -> day03Hours;
            case 4 -> day04Hours;
            case 5 -> day05Hours;
            case 6 -> day06Hours;
            case 7 -> day07Hours;
            case 8 -> day08Hours;
            case 9 -> day09Hours;
            case 10 -> day10Hours;
            case 11 -> day11Hours;
            case 12 -> day12Hours;
            case 13 -> day13Hours;
            case 14 -> day14Hours;
            case 15 -> day15Hours;
            case 16 -> day16Hours;
            case 17 -> day17Hours;
            case 18 -> day18Hours;
            case 19 -> day19Hours;
            case 20 -> day20Hours;
            case 21 -> day21Hours;
            case 22 -> day22Hours;
            case 23 -> day23Hours;
            case 24 -> day24Hours;
            case 25 -> day25Hours;
            case 26 -> day26Hours;
            case 27 -> day27Hours;
            case 28 -> day28Hours;
            case 29 -> day29Hours;
            case 30 -> day30Hours;
            case 31 -> day31Hours;
            default -> 0.0;
        };
    }

    /**
     * 특정 일의 근무시간을 설정하는 메서드
     */
    public void setDayHours(int day, Double hours) {
        switch (day) {
            case 1 -> this.day01Hours = hours;
            case 2 -> this.day02Hours = hours;
            case 3 -> this.day03Hours = hours;
            case 4 -> this.day04Hours = hours;
            case 5 -> this.day05Hours = hours;
            case 6 -> this.day06Hours = hours;
            case 7 -> this.day07Hours = hours;
            case 8 -> this.day08Hours = hours;
            case 9 -> this.day09Hours = hours;
            case 10 -> this.day10Hours = hours;
            case 11 -> this.day11Hours = hours;
            case 12 -> this.day12Hours = hours;
            case 13 -> this.day13Hours = hours;
            case 14 -> this.day14Hours = hours;
            case 15 -> this.day15Hours = hours;
            case 16 -> this.day16Hours = hours;
            case 17 -> this.day17Hours = hours;
            case 18 -> this.day18Hours = hours;
            case 19 -> this.day19Hours = hours;
            case 20 -> this.day20Hours = hours;
            case 21 -> this.day21Hours = hours;
            case 22 -> this.day22Hours = hours;
            case 23 -> this.day23Hours = hours;
            case 24 -> this.day24Hours = hours;
            case 25 -> this.day25Hours = hours;
            case 26 -> this.day26Hours = hours;
            case 27 -> this.day27Hours = hours;
            case 28 -> this.day28Hours = hours;
            case 29 -> this.day29Hours = hours;
            case 30 -> this.day30Hours = hours;
            case 31 -> this.day31Hours = hours;
        }
    }

    /**
     * 총 근무시간 계산 및 설정
     */
    public void calculateTotalWorkHours() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 1; i <= 31; i++) {
            Double dayHours = getDayHours(i);
            if (dayHours != null && dayHours > 0.0) {
                total = total.add(BigDecimal.valueOf(dayHours));
            }
        }
        this.totalWorkHours = total;
    }

    /**
     * 총 근무일수 계산 및 설정 (엑셀 수식과 동일: 근무시간이 있으면 1일로 계산)
     */
    public void calculateTotalWorkDays() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 1; i <= 31; i++) {
            Double dayHours = getDayHours(i);
            if (dayHours != null && dayHours > 0.0) {
                total = total.add(BigDecimal.ONE);
            }
        }
        this.totalWorkDays = total;
    }

    /**
     * 노무비 및 공제액 계산
     */
    public void calculatePayroll() {
        // 총 근무시간 및 일수 계산
        calculateTotalWorkHours();
        calculateTotalWorkDays();

        // 총 노무비 계산 (일당 기준)
        if (dailyWage != null && totalWorkDays != null) {
            this.totalLaborCost = BigDecimal.valueOf(dailyWage).multiply(totalWorkDays);
        } else {
            this.totalLaborCost = BigDecimal.ZERO;
        }

        // 공제액 계산
        if (totalLaborCost != null) {
            calculateDeductions();

            // 총 공제액 계산 및 저장
            this.totalDeductions = incomeTax
                    .add(employmentInsurance)
                    .add(healthInsurance)
                    .add(localTax)
                    .add(nationalPension)
                    .add(longTermCareInsurance);

            // 차감지급액 계산 (총 노무비 - 총 공제액)
            this.netPayment = totalLaborCost.subtract(this.totalDeductions);
        }
    }

    /**
     * 공제액 계산 (엑셀 수식 기준)
     */
    private void calculateDeductions() {
        // 해당 월 첫째 날 근무 여부 확인
        boolean workedOnFirstDay = day01Hours != null && day01Hours > 0.0;

        // 주민번호로 나이 계산
        int age = calculateAge();

        // 국민연금 상한액 (2025년 기준)
        BigDecimal pensionCeiling = new BigDecimal("6370000");
        BigDecimal pensionMaxAmount = new BigDecimal("286650");

        // 소득세 계산: 복잡한 엑셀 수식 구현
        BigDecimal calculatedIncomeTax = calculateIncomeTax(workedOnFirstDay);

        // 소득세 1000원 미만은 0으로 처리 (엑셀 수식: =IF(AN6<1000, 0, AN6))
        if (calculatedIncomeTax.compareTo(new BigDecimal("1000")) < 0) {
            this.incomeTax = BigDecimal.ZERO;
        } else {
            this.incomeTax = calculatedIncomeTax;
        }

        // 주민세: ROUNDDOWN(소득세 × 10%, -1) && 첫째날 근무해야 함
        if (!workedOnFirstDay) {
            this.localTax = BigDecimal.ZERO;
        } else {
            BigDecimal tax = incomeTax.multiply(new BigDecimal("0.1"));
            this.localTax = roundDown(tax, -1);
        }

        // 고용보험 (엑셀 수식):
        // =IFERROR(IF(AND($AM6<65),ROUNDDOWN(ROUNDDOWN(AA6*0.9%,-1),IF((AM6<65),0)),0),0)
        // $AM6: 만나이, AA6: 노무비 총액 - 65세 미만이면 고용보험 적용
        if (age < 65) {
            BigDecimal insurance = totalLaborCost.multiply(new BigDecimal("0.009"));
            this.employmentInsurance = roundDown(insurance, -1);
        } else {
            this.employmentInsurance = BigDecimal.ZERO;
        }

        // 건강보험: IF(총근무일수 >= 8, ROUNDDOWN(총노무비 × 3.545%, -1), 0)
        if (totalWorkDays.compareTo(new BigDecimal("8")) >= 0) {
            BigDecimal insurance = totalLaborCost.multiply(new BigDecimal("0.03545"));
            this.healthInsurance = roundDown(insurance, -1);
        } else {
            this.healthInsurance = BigDecimal.ZERO;
        }

        // 국민연금: 복잡한 조건
        if (age < 60 && (totalWorkDays.compareTo(new BigDecimal("8")) >= 0
                || totalLaborCost.compareTo(new BigDecimal("2200000")) >= 0)) {
            if (totalLaborCost.compareTo(pensionCeiling) <= 0) {
                // 상한액 이하: ROUNDDOWN(ROUNDDOWN(총노무비, -3) × 4.5%, -1)
                BigDecimal roundedCost = roundDown(totalLaborCost, -3);
                BigDecimal pension = roundedCost.multiply(new BigDecimal("0.045"));
                this.nationalPension = roundDown(pension, -1);
            } else {
                // 상한액 초과: 고정액
                this.nationalPension = pensionMaxAmount;
            }
        } else {
            this.nationalPension = BigDecimal.ZERO;
        }

        // 장기요양보험료 (엑셀 수식): IF(Z6>=8,ROUNDDOWN(AD6*0.9182%/7.09%,-1),0)
        // Z6: 총 일수, AD6: 건강보험료
        if (totalWorkDays.compareTo(new BigDecimal("8")) >= 0) {
            // 건강보험료 * (0.9182% / 7.09%) 계산
            BigDecimal rate = new BigDecimal("0.009182").divide(new BigDecimal("0.0709"), 10,
                    java.math.RoundingMode.HALF_UP);
            BigDecimal insurance = healthInsurance.multiply(rate);
            this.longTermCareInsurance = roundDown(insurance, -1);
        } else {
            this.longTermCareInsurance = BigDecimal.ZERO;
        }
    }

    /**
     * 생년월일 계산 (엑셀 수식과 동일)
     * 엑셀:
     * =IF(OR(MID(AK6,8,1)="1",MID(AK6,8,1)="2",MID(AK6,8,1)="5",MID(AK6,8,1)="6"),19&TEXT(LEFT(AK6,6),"00-00-00"),20&TEXT(LEFT(AK6,6),"00-00-00"))
     * AK6: 주민번호 (000000-0000000 형식)
     */
    private LocalDate calculateBirthDate() {
        if (labor == null || labor.getResidentNumber() == null || labor.getResidentNumber().length() < 13) {
            return LocalDate.of(1989, 1, 1); // 기본값
        }

        try {
            String residentNumber = labor.getResidentNumber().replace("-", "");
            if (residentNumber.length() < 13) {
                return LocalDate.of(1989, 1, 1); // 기본값
            }

            // 생년월일 추출 (처음 6자리)
            String yymmdd = residentNumber.substring(0, 6);
            int yy = Integer.parseInt(yymmdd.substring(0, 2));
            int mm = Integer.parseInt(yymmdd.substring(2, 4));
            int dd = Integer.parseInt(yymmdd.substring(4, 6));

            // 유효하지 않은 월/일 체크
            if (mm < 1 || mm > 12 || dd < 1 || dd > 31) {
                return LocalDate.of(1989, 1, 1); // 기본값
            }

            // 성별코드 (8번째 자리, 인덱스 7)
            String genderCode = residentNumber.substring(7, 8);

            // 엑셀 수식과 동일한 로직: 1,2,5,6이면 1900년대, 아니면 2000년대
            int fullYear;
            if ("1".equals(genderCode) || "2".equals(genderCode) ||
                    "5".equals(genderCode) || "6".equals(genderCode)) {
                fullYear = 1900 + yy;
            } else {
                fullYear = 2000 + yy;
            }

            // LocalDate 생성 시 예외 처리
            return LocalDate.of(fullYear, mm, dd);

        } catch (Exception e) {
            // 주민번호 파싱 오류 시 기본값 반환
            return LocalDate.of(1989, 1, 1);
        }
    }

    /**
     * 만나이 계산 (엑셀 수식과 동일: INT((TODAY()-생년월일)/365.25))
     */
    private int calculateAge() {
        LocalDate birthDate = calculateBirthDate();
        LocalDate today = LocalDate.now();

        // 엑셀 수식과 동일한 방식으로 나이 계산: INT((TODAY()-생년월일)/365.25)
        long daysDifference = java.time.temporal.ChronoUnit.DAYS.between(birthDate, today);
        int age = (int) (daysDifference / 365.25);

        return age;
    }

    /**
     * 소득세 계산 (엑셀 수식과 동일)
     * AO6: =ROUNDDOWN(IF($H6*I6>150000,($H6*I6-150000)*6%*45%,0),0)
     * H6: 일당, I6: 해당일에 공수 (여기서는 총 노무비 사용)
     */
    private BigDecimal calculateIncomeTax(boolean workedOnFirstDay) {
        if (totalLaborCost == null || !workedOnFirstDay) {
            return BigDecimal.ZERO;
        }

        BigDecimal threshold = new BigDecimal("150000");

        if (totalLaborCost.compareTo(threshold) > 0) {
            // (총노무비 - 150000) * 6% * 45%
            BigDecimal exceededAmount = totalLaborCost.subtract(threshold);
            BigDecimal tax = exceededAmount
                    .multiply(new BigDecimal("0.06"))
                    .multiply(new BigDecimal("0.45"));
            return roundDown(tax, 0);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * ROUNDDOWN 함수 구현
     * 
     * @param value  반올림할 값
     * @param digits 자릿수 (-1: 10의 자리, -2: 100의 자리)
     */
    private BigDecimal roundDown(BigDecimal value, int digits) {
        if (digits >= 0) {
            return value.setScale(digits, java.math.RoundingMode.DOWN);
        } else {
            BigDecimal divisor = BigDecimal.TEN.pow(-digits);
            return value.divide(divisor, 0, java.math.RoundingMode.DOWN).multiply(divisor);
        }
    }

    /**
     * LaborPayrollInfo로부터 업데이트
     */
    public void updateFrom(LaborPayrollInfo info) {
        // 1일~31일 공수 업데이트
        if (info.day01Hours() != null)
            this.day01Hours = info.day01Hours();
        if (info.day02Hours() != null)
            this.day02Hours = info.day02Hours();
        if (info.day03Hours() != null)
            this.day03Hours = info.day03Hours();
        if (info.day04Hours() != null)
            this.day04Hours = info.day04Hours();
        if (info.day05Hours() != null)
            this.day05Hours = info.day05Hours();
        if (info.day06Hours() != null)
            this.day06Hours = info.day06Hours();
        if (info.day07Hours() != null)
            this.day07Hours = info.day07Hours();
        if (info.day08Hours() != null)
            this.day08Hours = info.day08Hours();
        if (info.day09Hours() != null)
            this.day09Hours = info.day09Hours();
        if (info.day10Hours() != null)
            this.day10Hours = info.day10Hours();
        if (info.day11Hours() != null)
            this.day11Hours = info.day11Hours();
        if (info.day12Hours() != null)
            this.day12Hours = info.day12Hours();
        if (info.day13Hours() != null)
            this.day13Hours = info.day13Hours();
        if (info.day14Hours() != null)
            this.day14Hours = info.day14Hours();
        if (info.day15Hours() != null)
            this.day15Hours = info.day15Hours();
        if (info.day16Hours() != null)
            this.day16Hours = info.day16Hours();
        if (info.day17Hours() != null)
            this.day17Hours = info.day17Hours();
        if (info.day18Hours() != null)
            this.day18Hours = info.day18Hours();
        if (info.day19Hours() != null)
            this.day19Hours = info.day19Hours();
        if (info.day20Hours() != null)
            this.day20Hours = info.day20Hours();
        if (info.day21Hours() != null)
            this.day21Hours = info.day21Hours();
        if (info.day22Hours() != null)
            this.day22Hours = info.day22Hours();
        if (info.day23Hours() != null)
            this.day23Hours = info.day23Hours();
        if (info.day24Hours() != null)
            this.day24Hours = info.day24Hours();
        if (info.day25Hours() != null)
            this.day25Hours = info.day25Hours();
        if (info.day26Hours() != null)
            this.day26Hours = info.day26Hours();
        if (info.day27Hours() != null)
            this.day27Hours = info.day27Hours();
        if (info.day28Hours() != null)
            this.day28Hours = info.day28Hours();
        if (info.day29Hours() != null)
            this.day29Hours = info.day29Hours();
        if (info.day30Hours() != null)
            this.day30Hours = info.day30Hours();
        if (info.day31Hours() != null)
            this.day31Hours = info.day31Hours();

        // 일당 업데이트
        if (info.dailyWage() != null)
            this.dailyWage = info.dailyWage().intValue();

        // 계산된 값들 업데이트
        if (info.totalWorkHours() != null)
            this.totalWorkHours = info.totalWorkHours();
        if (info.totalWorkDays() != null)
            this.totalWorkDays = info.totalWorkDays();
        if (info.totalLaborCost() != null)
            this.totalLaborCost = info.totalLaborCost();

        // 공제 항목들 업데이트
        if (info.incomeTax() != null)
            this.incomeTax = info.incomeTax();
        if (info.employmentInsurance() != null)
            this.employmentInsurance = info.employmentInsurance();
        if (info.healthInsurance() != null)
            this.healthInsurance = info.healthInsurance();
        if (info.localTax() != null)
            this.localTax = info.localTax();
        if (info.nationalPension() != null)
            this.nationalPension = info.nationalPension();
        if (info.longTermCareInsurance() != null)
            this.longTermCareInsurance = info.longTermCareInsurance();
        if (info.totalDeductions() != null)
            this.totalDeductions = info.totalDeductions();
        if (info.netPayment() != null)
            this.netPayment = info.netPayment();

        // 비고 업데이트
        if (info.memo() != null)
            this.memo = info.memo();
    }
}
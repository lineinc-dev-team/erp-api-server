package com.lineinc.erp.api.server.domain.laborpayroll.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.DiffIgnore;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.experimental.SuperBuilder;

/**
 * 노무비 명세서 엔티티
 * 월별 인력별 근무 내역 및 급여 정보를 저장
 */
@Entity
@Table(name = "labor_payroll", indexes = {
        @Index(columnList = "year_month"),
        @Index(columnList = "labor_id, year_month"),
        @Index(columnList = "created_at"),
        @Index(columnList = "updated_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class LaborPayroll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "labor_payroll_seq")
    @SequenceGenerator(name = "labor_payroll_seq", sequenceName = "labor_payroll_seq", allocationSize = 1)
    private Long id;

    // 인력 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    private Labor labor;

    // 기본 정보
    @Column
    private YearMonth yearMonth; // 해당 년월

    // 일당
    @Column(precision = 10, scale = 2)
    private BigDecimal dailyWage;

    // 1일~31일까지의 공수 (근무시간)
    @Column(precision = 5, scale = 2)
    private BigDecimal day01Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day02Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day03Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day04Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day05Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day06Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day07Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day08Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day09Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day10Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day11Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day12Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day13Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day14Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day15Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day16Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day17Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day18Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day19Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day20Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day21Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day22Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day23Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day24Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day25Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day26Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day27Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day28Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day29Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day30Hours;

    @Column(precision = 5, scale = 2)
    private BigDecimal day31Hours;

    // 계산된 값들
    @Column(precision = 8, scale = 2)
    private BigDecimal totalWorkHours; // 총 근무시간

    @Column(precision = 6, scale = 2)
    private BigDecimal totalWorkDays; // 총 근무일수

    @Column(precision = 12, scale = 2)
    private BigDecimal totalLaborCost; // 총 노무비

    // 공제 항목들
    @Column(precision = 10, scale = 2)
    private BigDecimal incomeTax; // 소득세

    @Column(precision = 10, scale = 2)
    private BigDecimal employmentInsurance; // 고용보험료

    @Column(precision = 10, scale = 2)
    private BigDecimal healthInsurance; // 건강보험료

    @Column(precision = 10, scale = 2)
    private BigDecimal localTax; // 주민세

    @Column(precision = 10, scale = 2)
    private BigDecimal nationalPension; // 국민연금

    @Column(precision = 10, scale = 2)
    private BigDecimal longTermCareInsurance; // 장기요양보험료

    @Column(precision = 12, scale = 2)
    private BigDecimal netPayment; // 차감지급액

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    /**
     * 특정 일의 근무시간을 가져오는 메서드
     */
    public BigDecimal getDayHours(int day) {
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
            default -> BigDecimal.ZERO;
        };
    }

    /**
     * 특정 일의 근무시간을 설정하는 메서드
     */
    public void setDayHours(int day, BigDecimal hours) {
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
            BigDecimal dayHours = getDayHours(i);
            if (dayHours != null) {
                total = total.add(dayHours);
            }
        }
        this.totalWorkHours = total;
    }

    /**
     * 총 근무일수 계산 및 설정
     */
    public void calculateTotalWorkDays() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 1; i <= 31; i++) {
            BigDecimal dayHours = getDayHours(i);
            if (dayHours != null && dayHours.compareTo(BigDecimal.ZERO) > 0) {
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
            this.totalLaborCost = dailyWage.multiply(totalWorkDays);
        } else {
            this.totalLaborCost = BigDecimal.ZERO;
        }

        // 공제액 계산
        if (totalLaborCost != null) {
            this.incomeTax = totalLaborCost.multiply(new BigDecimal("0.033"));
            this.employmentInsurance = totalLaborCost.multiply(new BigDecimal("0.009"));
            this.healthInsurance = totalLaborCost.multiply(new BigDecimal("0.03545"));
            this.nationalPension = totalLaborCost.multiply(new BigDecimal("0.045"));
            this.localTax = incomeTax.multiply(new BigDecimal("0.1"));
            this.longTermCareInsurance = healthInsurance.multiply(new BigDecimal("0.1281"));

            // 차감지급액 계산 (총 노무비 - 모든 공제액)
            BigDecimal totalDeductions = incomeTax
                    .add(employmentInsurance)
                    .add(healthInsurance)
                    .add(localTax)
                    .add(nationalPension)
                    .add(longTermCareInsurance);

            this.netPayment = totalLaborCost.subtract(totalDeductions);
        }
    }
}
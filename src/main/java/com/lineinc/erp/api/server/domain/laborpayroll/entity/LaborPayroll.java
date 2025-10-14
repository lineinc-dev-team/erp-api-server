package com.lineinc.erp.api.server.domain.laborpayroll.entity;

import java.math.BigDecimal;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.laborpayroll.service.v1.LaborPayrollCalculator;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.laborpayroll.dto.request.LaborPayrollInfo;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
        @Index(columnList = "created_at"),
        @Index(columnList = "updated_at")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class LaborPayroll extends BaseEntity {

    private static final String SEQUENCE_NAME = "labor_payroll_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    // 인력 정보
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.LABOR_ID)
    private Labor labor;

    // 현장 정보
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_ID)
    private Site site;

    // 공정 정보
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_PROCESS_ID)
    private SiteProcess siteProcess;

    // 기본 정보
    private String yearMonth; // 해당 년월 (YYYY-MM 형식)

    // 일당
    @DiffInclude
    private Integer dailyWage;

    // 1일~31일까지의 공수 (근무시간)
    @DiffInclude
    private Double day01Hours;

    @DiffInclude
    private Double day02Hours;

    @DiffInclude
    private Double day03Hours;

    @DiffInclude
    private Double day04Hours;

    @DiffInclude
    private Double day05Hours;

    @DiffInclude
    private Double day06Hours;

    @DiffInclude
    private Double day07Hours;

    @DiffInclude
    private Double day08Hours;

    @DiffInclude
    private Double day09Hours;

    @DiffInclude
    private Double day10Hours;

    @DiffInclude
    private Double day11Hours;

    @DiffInclude
    private Double day12Hours;

    @DiffInclude
    private Double day13Hours;

    @DiffInclude
    private Double day14Hours;

    @DiffInclude
    private Double day15Hours;

    @DiffInclude
    private Double day16Hours;

    @DiffInclude
    private Double day17Hours;

    @DiffInclude
    private Double day18Hours;

    @DiffInclude
    private Double day19Hours;

    @DiffInclude
    private Double day20Hours;

    @DiffInclude
    private Double day21Hours;

    @DiffInclude
    private Double day22Hours;

    @DiffInclude
    private Double day23Hours;

    @DiffInclude
    private Double day24Hours;

    @DiffInclude
    private Double day25Hours;

    @DiffInclude
    private Double day26Hours;

    @DiffInclude
    private Double day27Hours;

    @DiffInclude
    private Double day28Hours;

    @DiffInclude
    private Double day29Hours;

    @DiffInclude
    private Double day30Hours;

    @DiffInclude
    private Double day31Hours;

    // 계산된 값들
    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal totalWorkHours; // 총 근무시간

    @DiffInclude
    @Column(precision = 6, scale = 2)
    private BigDecimal totalWorkDays; // 총 근무일수

    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal totalLaborCost; // 총 노무비

    // 공제 항목들
    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal incomeTax; // 소득세

    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal employmentInsurance; // 고용보험료

    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal healthInsurance; // 건강보험료

    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal localTax; // 주민세

    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal nationalPension; // 국민연금

    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal longTermCareInsurance; // 장기요양보험료

    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal totalDeductions; // 총 공제액

    @DiffInclude
    @Column(precision = 15, scale = 2)
    private BigDecimal netPayment; // 차감지급액

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    /**
     * 특정 일의 근무시간을 가져오는 메서드
     * 리플렉션을 사용해서 switch문 대신 동적으로 필드 접근
     */
    public Double getDayHours(final int day) {
        if (day < 1 || day > 31) {
            return 0.0;
        }

        try {
            final String fieldName = String.format("day%02dHours", day);
            final var field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (Double) field.get(this);
        } catch (final Exception e) {
            return 0.0;
        }
    }

    /**
     * 특정 일의 근무시간을 설정하는 메서드
     * 리플렉션을 사용해서 switch문 대신 동적으로 필드 설정
     */
    public void setDayHours(final int day, final Double hours) {
        if (day < 1 || day > 31) {
            return;
        }

        try {
            final String fieldName = String.format("day%02dHours", day);
            final var field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, hours);
        } catch (final Exception e) {
            // 필드 설정 실패 시 무시
        }
    }

    /**
     * 총 근무시간 계산 및 설정
     */
    public void calculateTotalWorkHours() {
        this.totalWorkHours = LaborPayrollCalculator.calculateTotalWorkHours(this);
    }

    /**
     * 총 근무일수 계산 및 설정 (엑셀 수식과 동일: 근무시간이 있으면 1일로 계산)
     */
    public void calculateTotalWorkDays() {
        this.totalWorkDays = LaborPayrollCalculator.calculateTotalWorkDays(this);
    }

    /**
     * 노무비 및 공제액 계산
     */
    public void calculatePayroll() {
        // 총 근무시간 및 일수 계산
        calculateTotalWorkHours();
        calculateTotalWorkDays();

        // 총 노무비 계산
        this.totalLaborCost = LaborPayrollCalculator.calculateTotalLaborCost(dailyWage, totalWorkHours);

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
        // 주민번호로 나이 계산
        final int age = LaborPayrollCalculator.calculateAge(labor);

        // 국민연금 상한액 (2025년 기준)
        final BigDecimal pensionCeiling = new BigDecimal("6370000");
        final BigDecimal pensionMaxAmount = new BigDecimal("286650");

        // 소득세 계산: 첫 번째 평일 근무 여부를 기준으로 계산
        final BigDecimal calculatedIncomeTax = LaborPayrollCalculator.calculateIncomeTax(dailyWage, this, yearMonth);

        // 소득세 1000원 미만은 0으로 처리 (엑셀 수식: =IF(AN6<1000, 0, AN6))
        if (calculatedIncomeTax.compareTo(new BigDecimal("1000")) < 0) {
            this.incomeTax = BigDecimal.ZERO;
        } else {
            this.incomeTax = calculatedIncomeTax;
        }

        // 주민세: ROUNDDOWN(소득세 × 10%, -1) && 첫 번째 평일 근무해야 함
        final boolean workedOnFirstWeekday = LaborPayrollCalculator.workedOnFirstWeekday(this, yearMonth);
        if (!workedOnFirstWeekday || incomeTax.compareTo(BigDecimal.ZERO) == 0) {
            this.localTax = BigDecimal.ZERO;
        } else {
            final BigDecimal tax = incomeTax.multiply(new BigDecimal("0.1"));
            this.localTax = LaborPayrollCalculator.roundDown(tax, -1);
        }

        // 고용보험 (엑셀 수식):
        // =IFERROR(IF(AND($AM6<65),ROUNDDOWN(ROUNDDOWN(AA6*0.9%,-1),IF((AM6<65),0)),0),0)
        // $AM6: 만나이, AA6: 노무비 총액 - 65세 미만이면 고용보험 적용
        if (age < 65) {
            final BigDecimal insurance = totalLaborCost.multiply(new BigDecimal("0.009"));
            this.employmentInsurance = LaborPayrollCalculator.roundDown(insurance, -1);
        } else {
            this.employmentInsurance = BigDecimal.ZERO;
        }

        // 건강보험: IF(총근무일수 >= 8, ROUNDDOWN(총노무비 × 3.545%, -1), 0)
        if (totalWorkDays.compareTo(new BigDecimal("8")) >= 0) {
            final BigDecimal insurance = totalLaborCost.multiply(new BigDecimal("0.03545"));
            this.healthInsurance = LaborPayrollCalculator.roundDown(insurance, -1);
        } else {
            this.healthInsurance = BigDecimal.ZERO;
        }

        // 국민연금: 복잡한 조건
        if (age < 60 && (totalWorkDays.compareTo(new BigDecimal("8")) >= 0
                || totalLaborCost.compareTo(new BigDecimal("2200000")) >= 0)) {
            if (totalLaborCost.compareTo(pensionCeiling) <= 0) {
                // 상한액 이하: ROUNDDOWN(ROUNDDOWN(총노무비, -3) × 4.5%, -1)
                final BigDecimal roundedCost = LaborPayrollCalculator.roundDown(totalLaborCost, -3);
                final BigDecimal pension = roundedCost.multiply(new BigDecimal("0.045"));
                this.nationalPension = LaborPayrollCalculator.roundDown(pension, -1);
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
            final BigDecimal rate = new BigDecimal("0.009182").divide(new BigDecimal("0.0709"), 10,
                    java.math.RoundingMode.HALF_UP);
            final BigDecimal insurance = healthInsurance.multiply(rate);
            this.longTermCareInsurance = LaborPayrollCalculator.roundDown(insurance, -1);
        } else {
            this.longTermCareInsurance = BigDecimal.ZERO;
        }
    }

    /**
     * LaborPayrollInfo로부터 업데이트
     * 리플렉션을 사용해서 반복문으로 간소화
     */
    public void updateFrom(final LaborPayrollInfo info) {
        // 1일~31일 공수 업데이트 (리플렉션 사용)
        updateDayHoursFromInfo(info);

        // 일당 업데이트
        if (info.dailyWage() != null) {
            this.dailyWage = info.dailyWage().intValue();
        }

        // 계산된 값들 업데이트
        if (info.totalWorkHours() != null) {
            this.totalWorkHours = info.totalWorkHours();
        }
        if (info.totalWorkDays() != null) {
            this.totalWorkDays = info.totalWorkDays();
        }
        if (info.totalLaborCost() != null) {
            this.totalLaborCost = info.totalLaborCost();
        }

        // 공제 항목들 업데이트
        if (info.incomeTax() != null) {
            this.incomeTax = info.incomeTax();
        }
        if (info.employmentInsurance() != null) {
            this.employmentInsurance = info.employmentInsurance();
        }
        if (info.healthInsurance() != null) {
            this.healthInsurance = info.healthInsurance();
        }
        if (info.localTax() != null) {
            this.localTax = info.localTax();
        }
        if (info.nationalPension() != null) {
            this.nationalPension = info.nationalPension();
        }
        if (info.longTermCareInsurance() != null) {
            this.longTermCareInsurance = info.longTermCareInsurance();
        }
        if (info.totalDeductions() != null) {
            this.totalDeductions = info.totalDeductions();
        }
        if (info.netPayment() != null) {
            this.netPayment = info.netPayment();
        }

        // 비고 업데이트
        if (info.memo() != null) {
            this.memo = info.memo();
        }
    }

    /**
     * LaborPayrollInfo의 일별 근무시간을 리플렉션으로 업데이트
     */
    private void updateDayHoursFromInfo(final LaborPayrollInfo info) {
        try {
            for (int day = 1; day <= 31; day++) {
                final String fieldName = String.format("day%02dHours", day);
                final String methodName = fieldName; // record의 getter는 필드명과 동일

                final var method = info.getClass().getMethod(methodName);
                final Double dayHours = (Double) method.invoke(info);

                if (dayHours != null) {
                    setDayHours(day, dayHours);
                }
            }
        } catch (final Exception e) {
            // 리플렉션 실패 시 무시
        }
    }
}
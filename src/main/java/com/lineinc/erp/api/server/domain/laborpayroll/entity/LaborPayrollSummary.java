package com.lineinc.erp.api.server.domain.laborpayroll.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;

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
import lombok.experimental.SuperBuilder;

/**
 * 노무명세서 집계 테이블
 * 월별, 현장별, 공정별 노무비 통계 정보를 사전에 계산하여 저장
 */
@Entity
@Table(name = "labor_payroll_summary", indexes = {
        @Index(columnList = "year_month"),
        @Index(columnList = "site_id, year_month"),
        @Index(columnList = "site_process_id, year_month"),
        @Index(columnList = "created_at"),
        @Index(columnList = "updated_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class LaborPayrollSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "labor_payroll_summary_seq")
    @SequenceGenerator(name = "labor_payroll_summary_seq", sequenceName = "labor_payroll_summary_seq", allocationSize = 1)
    private Long id;

    // 현장 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    // 공정 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id", nullable = false)
    private SiteProcess siteProcess;

    // 기본 정보
    @Column(length = 7)
    private String yearMonth; // 해당 년월 (YYYY-MM 형식)

    // 인력 수 집계
    @Column
    private Integer regularEmployeeCount; // 정직원 수

    @Column
    private Integer directContractCount; // 직영/계약직 수

    @Column
    private Integer etcCount; // 기타 수

    // 금액 집계
    @Column(precision = 15, scale = 2)
    private BigDecimal totalLaborCost; // 총 노무비

    @Column(precision = 15, scale = 2)
    private BigDecimal totalDeductions; // 총 공제액

    @Column(precision = 15, scale = 2)
    private BigDecimal totalNetPayment; // 총 차감지급액

    /**
     * 집계 데이터 업데이트
     */
    public void updateSummary(
            Integer regularEmployeeCount,
            Integer directContractCount,
            Integer etcCount,
            BigDecimal totalLaborCost,
            BigDecimal totalDeductions,
            BigDecimal totalNetPayment) {

        this.regularEmployeeCount = regularEmployeeCount;
        this.directContractCount = directContractCount;
        this.etcCount = etcCount;
        this.totalLaborCost = totalLaborCost;
        this.totalDeductions = totalDeductions;
        this.totalNetPayment = totalNetPayment;

    }
}

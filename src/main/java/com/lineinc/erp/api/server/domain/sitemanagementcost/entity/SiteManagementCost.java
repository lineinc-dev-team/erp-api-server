package com.lineinc.erp.api.server.domain.sitemanagementcost.entity;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
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
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 현장/본사 관리비 엔티티
 */
@Entity
@Table(indexes = {
        @Index(columnList = "year_month"),
        @Index(columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class SiteManagementCost extends BaseEntity {
    private static final String SEQUENCE_NAME = "site_management_cost_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 년월 (예: 2025-01)
     */
    @Column(nullable = false)
    @DiffInclude
    private String yearMonth;

    /**
     * 현장
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    @DiffIgnore
    private Site site;

    /**
     * 공정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id")
    @DiffIgnore
    private SiteProcess siteProcess;

    // === 현장관리비 ===

    /**
     * 직원급여
     */
    @Column
    @DiffInclude
    private Long employeeSalary;

    /**
     * 퇴직연금(정규직)
     */
    @Column
    @DiffInclude
    private Long regularRetirementPension;

    /**
     * 퇴직공제부금
     */
    @Column
    @DiffInclude
    private Long retirementDeduction;

    /**
     * 4대보험(상용)
     */
    @Column
    @DiffInclude
    private Long majorInsuranceRegular;

    /**
     * 4대보험(일용)
     */
    @Column
    @DiffInclude
    private Long majorInsuranceDaily;

    /**
     * 보증수수료(계약보증)
     */
    @Column
    @DiffInclude
    private Long contractGuaranteeFee;

    /**
     * 보증수수료(현장별건설기계)
     */
    @Column
    @DiffInclude
    private Long equipmentGuaranteeFee;

    /**
     * 국세납부
     */
    @Column
    @DiffInclude
    private Long nationalTaxPayment;

    // === 본사관리비 ===

    /**
     * 본사관리비
     */
    @Column
    @DiffInclude
    private Long headquartersManagementCost;

    /**
     * 비고
     */
    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo;

    // === Transient 필드 (Javers 감사 로그용) ===

    @Transient
    @DiffInclude
    private String siteName;

    @Transient
    @DiffInclude
    private String processName;

    /**
     * Javers 감사 로그를 위한 transient 필드 동기화
     */
    public void syncTransientFields() {
        this.siteName = this.site != null ? this.site.getName() : null;
        this.processName = this.siteProcess != null ? this.siteProcess.getName() : null;
    }

    /**
     * 현장관리비 합계 계산
     */
    public Long calculateSiteManagementTotal() {
        long total = 0L;
        if (employeeSalary != null)
            total += employeeSalary;
        if (regularRetirementPension != null)
            total += regularRetirementPension;
        if (retirementDeduction != null)
            total += retirementDeduction;
        if (majorInsuranceRegular != null)
            total += majorInsuranceRegular;
        if (majorInsuranceDaily != null)
            total += majorInsuranceDaily;
        if (contractGuaranteeFee != null)
            total += contractGuaranteeFee;
        if (equipmentGuaranteeFee != null)
            total += equipmentGuaranteeFee;
        if (nationalTaxPayment != null)
            total += nationalTaxPayment;
        return total;
    }

    /**
     * 전체 관리비 합계 계산 (현장관리비 + 본사관리비)
     */
    public Long calculateTotalManagementCost() {
        long total = calculateSiteManagementTotal();
        if (headquartersManagementCost != null)
            total += headquartersManagementCost;
        return total;
    }
}

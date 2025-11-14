package com.lineinc.erp.api.server.domain.dashboard.entity;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 현장별 월별 비용 집계 엔티티
 * 대시보드 현장 목록에 표시되는 현장들의 월별 비용을 저장합니다.
 */
@Entity
@Table(name = "site_monthly_cost_summary", indexes = {
        @Index(columnList = "year_month"),
        @Index(columnList = "site_id, site_process_id, year_month"),
        @Index(columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class SiteMonthlyCostSummary extends BaseEntity {

    private static final String SEQUENCE_NAME = "site_monthly_cost_summary_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 년월 (예: 2025-01)
     */
    @Column(nullable = false, length = 7)
    @DiffInclude
    private String yearMonth;

    /**
     * 현장
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_ID, nullable = false)
    private Site site;

    /**
     * 공정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_PROCESS_ID, nullable = false)
    private SiteProcess siteProcess;

    /**
     * 재료비 (원)
     */
    @Column
    @DiffInclude
    private Long materialCost;

    /**
     * 노무비 (원)
     */
    @Column
    @DiffInclude
    private Long laborCost;

    /**
     * 관리비 (원)
     */
    @Column
    @DiffInclude
    private Long managementCost;

    /**
     * 장비비 (원)
     */
    @Column
    @DiffInclude
    private Long equipmentCost;

    /**
     * 외주비 (원)
     */
    @Column
    @DiffInclude
    private Long outsourcingCost;

    /**
     * 비용 집계 데이터 업데이트
     */
    public void updateCosts(
            final Long materialCost,
            final Long laborCost,
            final Long managementCost,
            final Long equipmentCost,
            final Long outsourcingCost) {
        this.materialCost = materialCost;
        this.laborCost = laborCost;
        this.managementCost = managementCost;
        this.equipmentCost = equipmentCost;
        this.outsourcingCost = outsourcingCost;
    }
}

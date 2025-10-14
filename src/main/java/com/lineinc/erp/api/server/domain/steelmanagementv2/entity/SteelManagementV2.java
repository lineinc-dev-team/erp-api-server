package com.lineinc.erp.api.server.domain.steelmanagementv2.entity;

import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Category;
import com.lineinc.erp.api.server.domain.steelmanagementv2.enums.SteelManagementDetailV2Type;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 강재수불부 V2 엔티티
 */
@Entity
@Table(name = "steel_management_v2", indexes = {
        @Index(columnList = "created_at"),
        @Index(columnList = "updated_at"),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class SteelManagementV2 extends BaseEntity {

    private static final String SEQUENCE_NAME = "steel_management_v2_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 현장 참조
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    /**
     * 공정 참조
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id")
    private SiteProcess siteProcess;

    /**
     * 강재수불부 상세 항목 목록
     */
    @DiffIgnore
    @OneToMany(mappedBy = "steelManagementV2", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteelManagementDetailV2> details = new ArrayList<>();

    // ===== 집계 필드 =====
    // 입고 집계
    /**
     * 입고 자사자재 총 무게(톤)
     */
    @Column
    private Double incomingOwnMaterialTotalWeight;
    /**
     * 입고 자사자재 총 금액
     */
    @Column
    private Long incomingOwnMaterialAmount;
    /**
     * 입고 구매자재 총 무게(톤)
     */
    @Column
    private Double incomingPurchaseTotalWeight;
    /**
     * 입고 구매자재 총 금액
     */
    @Column
    private Long incomingPurchaseAmount;
    /**
     * 입고 임대자재 총 무게(톤)
     */
    @Column
    private Double incomingRentalTotalWeight;
    /**
     * 입고 임대자재 총 금액
     */
    @Column
    private Long incomingRentalAmount;

    // 출고 집계
    /**
     * 출고 자사자재 총 무게(톤)
     */
    @Column
    private Double outgoingOwnMaterialTotalWeight;
    /**
     * 출고 자사자재 총 금액
     */
    @Column
    private Long outgoingOwnMaterialAmount;
    /**
     * 출고 구매자재 총 무게(톤)
     */
    @Column
    private Double outgoingPurchaseTotalWeight;
    /**
     * 출고 구매자재 총 금액
     */
    @Column
    private Long outgoingPurchaseAmount;
    /**
     * 출고 임대자재 총 무게(톤)
     */
    @Column
    private Double outgoingRentalTotalWeight;
    /**
     * 출고 임대자재 총 금액
     */
    @Column
    private Long outgoingRentalAmount;

    // 사장 집계
    /**
     * 사장(현장 적치) 총 무게(톤)
     * 땅에 박아놓은 강재의 무게
     */
    @Column
    private Double onSiteStockTotalWeight;

    // 고철 집계
    /**
     * 고철 총 무게(톤)
     */
    @Column
    private Double scrapTotalWeight;
    /**
     * 고철 총 금액
     */
    @Column
    private Long scrapAmount;

    // 최종 집계
    /**
     * 총 금액(투입비)
     * 계산식: 입고 소계 + 출고 소계 - 고철
     */
    @Column
    private Long totalInvestmentAmount;
    /**
     * 현장보류수량 (톤)
     * 계산식: 입고 소계 총무게 - 출고 소계 총무게 - 고철 총무게
     */
    @Column
    private Double onSiteRemainingWeight;

    /**
     * 상세 항목을 기반으로 집계 값을 계산
     */
    public void calculateAggregations() {
        // 입고 집계
        this.incomingOwnMaterialTotalWeight = calculateTotalWeight(
                SteelManagementDetailV2Type.INCOMING,
                SteelManagementDetailV2Category.OWN_MATERIAL);
        this.incomingOwnMaterialAmount = calculateAmount(
                SteelManagementDetailV2Type.INCOMING,
                SteelManagementDetailV2Category.OWN_MATERIAL);
        this.incomingPurchaseTotalWeight = calculateTotalWeight(
                SteelManagementDetailV2Type.INCOMING,
                SteelManagementDetailV2Category.PURCHASE);
        this.incomingPurchaseAmount = calculateAmount(
                SteelManagementDetailV2Type.INCOMING,
                SteelManagementDetailV2Category.PURCHASE);
        this.incomingRentalTotalWeight = calculateTotalWeight(
                SteelManagementDetailV2Type.INCOMING,
                SteelManagementDetailV2Category.RENTAL);
        this.incomingRentalAmount = calculateAmount(
                SteelManagementDetailV2Type.INCOMING,
                SteelManagementDetailV2Category.RENTAL);

        // 출고 집계
        this.outgoingOwnMaterialTotalWeight = calculateTotalWeight(
                SteelManagementDetailV2Type.OUTGOING,
                SteelManagementDetailV2Category.OWN_MATERIAL);
        this.outgoingOwnMaterialAmount = calculateAmount(
                SteelManagementDetailV2Type.OUTGOING,
                SteelManagementDetailV2Category.OWN_MATERIAL);
        this.outgoingPurchaseTotalWeight = calculateTotalWeight(
                SteelManagementDetailV2Type.OUTGOING,
                SteelManagementDetailV2Category.PURCHASE);
        this.outgoingPurchaseAmount = calculateAmount(
                SteelManagementDetailV2Type.OUTGOING,
                SteelManagementDetailV2Category.PURCHASE);
        this.outgoingRentalTotalWeight = calculateTotalWeight(
                SteelManagementDetailV2Type.OUTGOING,
                SteelManagementDetailV2Category.RENTAL);
        this.outgoingRentalAmount = calculateAmount(
                SteelManagementDetailV2Type.OUTGOING,
                SteelManagementDetailV2Category.RENTAL);

        // 사장 집계
        this.onSiteStockTotalWeight = calculateTotalWeightByType(
                SteelManagementDetailV2Type.ON_SITE_STOCK);

        // 고철 집계
        this.scrapTotalWeight = calculateTotalWeightByType(
                SteelManagementDetailV2Type.SCRAP);
        this.scrapAmount = calculateAmountByType(
                SteelManagementDetailV2Type.SCRAP);

        // 최종 집계
        // 입고 소계
        final long incomingSubtotal = (this.incomingOwnMaterialAmount != null ? this.incomingOwnMaterialAmount : 0L)
                + (this.incomingPurchaseAmount != null ? this.incomingPurchaseAmount : 0L)
                + (this.incomingRentalAmount != null ? this.incomingRentalAmount : 0L);

        // 출고 소계
        final long outgoingSubtotal = (this.outgoingOwnMaterialAmount != null ? this.outgoingOwnMaterialAmount : 0L)
                + (this.outgoingPurchaseAmount != null ? this.outgoingPurchaseAmount : 0L)
                + (this.outgoingRentalAmount != null ? this.outgoingRentalAmount : 0L);

        // 총 금액(투입비) = 입고 소계(금액) + 출고 소계(금액) - 고철(금액)
        this.totalInvestmentAmount = incomingSubtotal + outgoingSubtotal
                - (this.scrapAmount != null ? this.scrapAmount : 0L);

        // 현장보류수량(톤) = 입고 소계(총무게) - 출고 소계(총무게) - 고철(총무게)
        final double incomingWeightSubtotal = (this.incomingOwnMaterialTotalWeight != null
                ? this.incomingOwnMaterialTotalWeight
                : 0.0)
                + (this.incomingPurchaseTotalWeight != null ? this.incomingPurchaseTotalWeight : 0.0)
                + (this.incomingRentalTotalWeight != null ? this.incomingRentalTotalWeight : 0.0);

        final double outgoingWeightSubtotal = (this.outgoingOwnMaterialTotalWeight != null
                ? this.outgoingOwnMaterialTotalWeight
                : 0.0)
                + (this.outgoingPurchaseTotalWeight != null ? this.outgoingPurchaseTotalWeight : 0.0)
                + (this.outgoingRentalTotalWeight != null ? this.outgoingRentalTotalWeight : 0.0);

        final double remaining = incomingWeightSubtotal - outgoingWeightSubtotal
                - (this.scrapTotalWeight != null ? this.scrapTotalWeight : 0.0);

        // 소수점 4자리에서 반올림
        this.onSiteRemainingWeight = Math.round(remaining * 10000.0) / 10000.0;
    }

    /**
     * 특정 타입과 카테고리의 총 무게를 계산
     *
     * @param type     상세 타입 (입고/출고/사장/고철)
     * @param category 상세 카테고리 (자사자재/구매/임대)
     * @return 총 무게(톤)
     */
    private Double calculateTotalWeight(
            final SteelManagementDetailV2Type type,
            final SteelManagementDetailV2Category category) {
        return details.stream()
                .filter(detail -> !detail.isDeleted() && detail.getType() == type && detail.getCategory() == category)
                .mapToDouble(detail -> detail.getTotalWeight() != null ? detail.getTotalWeight() : 0.0)
                .sum();
    }

    /**
     * 특정 타입과 카테고리의 총 금액을 계산
     *
     * @param type     상세 타입 (입고/출고/사장/고철)
     * @param category 상세 카테고리 (자사자재/구매/임대)
     * @return 총 금액
     */
    private Long calculateAmount(
            final SteelManagementDetailV2Type type,
            final SteelManagementDetailV2Category category) {
        return details.stream()
                .filter(detail -> !detail.isDeleted() && detail.getType() == type && detail.getCategory() == category)
                .mapToLong(detail -> detail.getAmount() != null ? detail.getAmount() : 0L)
                .sum();
    }

    /**
     * 특정 타입의 총 무게를 계산 (카테고리 무관)
     *
     * @param type 상세 타입 (입고/출고/사장/고철)
     * @return 총 무게(톤)
     */
    private Double calculateTotalWeightByType(
            final SteelManagementDetailV2Type type) {
        return details.stream()
                .filter(detail -> !detail.isDeleted() && detail.getType() == type)
                .mapToDouble(detail -> detail.getTotalWeight() != null ? detail.getTotalWeight() : 0.0)
                .sum();
    }

    /**
     * 특정 타입의 총 금액을 계산 (카테고리 무관)
     *
     * @param type 상세 타입 (입고/출고/사장/고철)
     * @return 총 금액
     */
    private Long calculateAmountByType(
            final SteelManagementDetailV2Type type) {
        return details.stream()
                .filter(detail -> !detail.isDeleted() && detail.getType() == type)
                .mapToLong(detail -> detail.getAmount() != null ? detail.getAmount() : 0L)
                .sum();
    }
}

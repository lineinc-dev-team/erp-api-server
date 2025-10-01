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
    @Column
    private Double incomingOwnMaterialTotalWeight;
    @Column
    private Long incomingOwnMaterialAmount;
    @Column
    private Double incomingPurchaseTotalWeight;
    @Column
    private Long incomingPurchaseAmount;
    @Column
    private Double incomingRentalTotalWeight;
    @Column
    private Long incomingRentalAmount;

    // 출고 집계
    @Column
    private Double outgoingOwnMaterialTotalWeight;
    @Column
    private Long outgoingOwnMaterialAmount;
    @Column
    private Double outgoingPurchaseTotalWeight;
    @Column
    private Long outgoingPurchaseAmount;
    @Column
    private Double outgoingRentalTotalWeight;
    @Column
    private Long outgoingRentalAmount;

    // 사장 집계
    @Column
    private Double onSiteStockTotalWeight;

    // 고철 집계
    @Column
    private Double scrapTotalWeight;
    @Column
    private Long scrapAmount;

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
    }

    private Double calculateTotalWeight(
            final SteelManagementDetailV2Type type,
            final SteelManagementDetailV2Category category) {
        return details.stream()
                .filter(detail -> !detail.isDeleted() && detail.getType() == type && detail.getCategory() == category)
                .mapToDouble(detail -> detail.getTotalWeight() != null ? detail.getTotalWeight() : 0.0)
                .sum();
    }

    private Long calculateAmount(
            final SteelManagementDetailV2Type type,
            final SteelManagementDetailV2Category category) {
        return details.stream()
                .filter(detail -> !detail.isDeleted() && detail.getType() == type && detail.getCategory() == category)
                .mapToLong(detail -> detail.getAmount() != null ? detail.getAmount() : 0L)
                .sum();
    }

    private Double calculateTotalWeightByType(
            final SteelManagementDetailV2Type type) {
        return details.stream()
                .filter(detail -> !detail.isDeleted() && detail.getType() == type)
                .mapToDouble(detail -> detail.getTotalWeight() != null ? detail.getTotalWeight() : 0.0)
                .sum();
    }

    private Long calculateAmountByType(
            final SteelManagementDetailV2Type type) {
        return details.stream()
                .filter(detail -> !detail.isDeleted() && detail.getType() == type)
                .mapToLong(detail -> detail.getAmount() != null ? detail.getAmount() : 0L)
                .sum();
    }
}

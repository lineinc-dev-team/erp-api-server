package com.lineinc.erp.api.server.domain.managementcost.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(indexes = {
        @Index(columnList = "payment_date"),
        @Index(columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class ManagementCost extends BaseEntity {
    private static final String SEQUENCE_NAME = "management_cost_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_ID)
    @DiffIgnore
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_PROCESS_ID)
    @DiffIgnore
    private SiteProcess siteProcess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    @DiffIgnore
    private OutsourcingCompany outsourcingCompany;

    /**
     * 항목 타입 (예: 전기, 수도 등)
     */
    @Enumerated(EnumType.STRING)
    @Column
    @DiffIgnore
    private ManagementCostItemType itemType;

    /**
     * 항목 설명 (예: 6월 전기요금 등)
     */
    @Column
    @DiffInclude
    private String itemTypeDescription;

    /**
     * 관리비가 발생(결제)된 날짜
     */
    @Column
    @DiffIgnore
    private OffsetDateTime paymentDate;

    @DiffIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = AppConstants.MANAGEMENT_COST_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostFile> files = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = AppConstants.MANAGEMENT_COST_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostDetail> details = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = AppConstants.MANAGEMENT_COST_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostKeyMoneyDetail> keyMoneyDetails = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = AppConstants.MANAGEMENT_COST_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostMealFeeDetail> mealFeeDetails = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = AppConstants.MANAGEMENT_COST_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostMealFeeDetailDirectContract> mealFeeDetailDirectContracts = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo;

    @Transient
    @DiffInclude
    private String siteName;

    @Transient
    @DiffInclude
    private String processName;

    @Transient
    @DiffInclude
    private String outsourcingCompanyName;

    @Transient
    @DiffInclude
    private String paymentDateFormat;

    @Transient
    @DiffInclude
    private String itemTypeName;

    public void updateFrom(final ManagementCostUpdateRequest request, final Site site, final SiteProcess siteProcess,
            final OutsourcingCompany outsourcingCompany) {
        this.site = site;
        this.siteProcess = siteProcess;
        this.outsourcingCompany = outsourcingCompany;
        this.itemTypeDescription = request.itemTypeDescription();
        this.paymentDate = DateTimeFormatUtils.toOffsetDateTime(request.paymentDate());
        this.memo = request.memo();
        syncTransientFields();
    }

    /**
     * Javers 감사 로그를 위한 transient 필드 동기화
     */
    public void syncTransientFields() {
        this.siteName = this.site != null ? this.site.getName() : null;
        this.processName = this.siteProcess != null ? this.siteProcess.getName() : null;
        this.outsourcingCompanyName = this.outsourcingCompany != null ? this.outsourcingCompany.getName() : null;
        this.paymentDateFormat = this.paymentDate != null ? DateTimeFormatUtils.formatKoreaLocalDate(this.paymentDate)
                : null;
        this.itemTypeName = this.itemType != null ? this.itemType.getLabel() : null;
    }
}
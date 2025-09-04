package com.lineinc.erp.api.server.domain.managementcost.entity;

import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostUpdateRequest;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.DiffIgnore;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Transient;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class ManagementCost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "management_cost_seq")
    @SequenceGenerator(name = "management_cost_seq", sequenceName = "management_cost_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    @DiffIgnore
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id")
    @DiffIgnore
    private SiteProcess siteProcess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    @DiffIgnore
    private OutsourcingCompany outsourcingCompany;

    /**
     * 항목 타입 (예: 전기, 수도 등)
     */
    @Enumerated(EnumType.STRING)
    @Column
    @DiffIgnore
    private ItemType itemType;

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
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "managementCost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostFile> files = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "managementCost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostDetail> details = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "managementCost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostKeyMoneyDetail> keyMoneyDetails = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "managementCost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementCostMealFeeDetail> mealFeeDetails = new ArrayList<>();

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

    public void updateFrom(ManagementCostUpdateRequest request, Site site, SiteProcess siteProcess,
            OutsourcingCompany outsourcingCompany) {
        this.site = site;
        this.siteProcess = siteProcess;
        this.outsourcingCompany = outsourcingCompany;

        if (request.itemTypeDescription() != null) {
            this.itemTypeDescription = request.itemTypeDescription();
        }
        if (request.paymentDate() != null) {
            this.paymentDate = DateTimeFormatUtils.toOffsetDateTime(request.paymentDate());
        }
        if (request.memo() != null) {
            this.memo = request.memo();
        }

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
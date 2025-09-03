package com.lineinc.erp.api.server.domain.steelmanagement.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.steelmanagement.enums.SteelManagementType;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementUpdateRequest;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class SteelManagement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "steel_management_seq")
    @SequenceGenerator(name = "steel_management_seq", sequenceName = "steel_management_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id")
    private SiteProcess siteProcess;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    private OutsourcingCompany outsourcingCompany;

    @DiffIgnore
    @OneToMany(mappedBy = "steelManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteelManagementFile> files = new ArrayList<>();

    @DiffIgnore
    @OneToMany(mappedBy = "steelManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SteelManagementDetail> details = new ArrayList<>();

    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Setter
    @Column
    private SteelManagementType type;

    /**
     * 이전 강재 수불 구분
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    @Column
    private SteelManagementType previousType;

    /**
     * 기간 시작일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime startDate;

    /**
     * 기간 종료일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime endDate;

    /**
     * 주문일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime orderDate;

    /**
     * 승인일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime approvalDate;

    /**
     * 반출일
     */
    @DiffIgnore
    @Column
    private OffsetDateTime releaseDate;

    /**
     * 용도
     */
    @DiffInclude
    @Column
    private String usage;

    /**
     * 비고
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
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
    private String typeName;

    @Transient
    @DiffInclude
    private String startDateFormat;

    @Transient
    @DiffInclude
    private String endDateFormat;

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        this.siteName = this.site != null ? this.site.getName() : null;
        this.processName = this.siteProcess != null ? this.siteProcess.getName() : null;
        this.outsourcingCompanyName = this.outsourcingCompany != null ? this.outsourcingCompany.getName() : null;
        this.typeName = this.type != null ? this.type.getLabel() : null;
        this.startDateFormat = this.startDate != null
                ? DateTimeFormatUtils.formatKoreaLocalDate(this.startDate)
                : null;
        this.endDateFormat = this.endDate != null
                ? DateTimeFormatUtils.formatKoreaLocalDate(this.endDate)
                : null;
    }

    public void updateFrom(SteelManagementUpdateRequest request, Site site, SiteProcess siteProcess,
            OutsourcingCompany outsourcingCompany) {
        if (site != null) {
            this.site = site;
        }
        if (siteProcess != null) {
            this.siteProcess = siteProcess;
        }
        if (outsourcingCompany != null) {
            this.outsourcingCompany = outsourcingCompany;
        }

        Optional.ofNullable(request.usage()).ifPresent(val -> this.usage = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        Optional.ofNullable(request.startDate())
                .map(DateTimeFormatUtils::toOffsetDateTime)
                .ifPresent(val -> this.startDate = val);
        Optional.ofNullable(request.endDate())
                .map(DateTimeFormatUtils::toOffsetDateTime)
                .ifPresent(val -> this.endDate = val);

        syncTransientFields();
    }

    public void changeType(SteelManagementType newType) {
        // 반출 상태로 변경할 때는 previousType을 변경하지 않음
        if (newType != SteelManagementType.RELEASE && this.type != null && !this.type.equals(newType)) {
            this.previousType = this.type;
        }

        // 승인 상태로 변경될 때 승인일 설정
        if (newType == SteelManagementType.APPROVAL) {
            this.approvalDate = OffsetDateTime.now();
        }

        // 반출 상태로 변경될 때 반출일 설정
        if (newType == SteelManagementType.RELEASE) {
            this.releaseDate = OffsetDateTime.now();
        }

        this.type = newType;
    }
}

package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementInputType;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementUpdateRequest;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class MaterialManagement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_management_seq")
    @SequenceGenerator(name = "material_management_seq", sequenceName = "material_management_seq", allocationSize = 1)
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
    @Column
    private MaterialManagementInputType inputType;

    /**
     * 투입구분 상세 설명 (예: 자재 직접 반입, 외주 업체 제공 등)
     */
    @DiffInclude
    @Column
    private String inputTypeDescription;

    /**
     * 납품일자
     */
    @DiffInclude
    @Column(nullable = false)
    private OffsetDateTime deliveryDate;

    /**
     * 비고
     */
    @DiffInclude
    @Column
    private String memo;

    @DiffIgnore
    @OneToMany(mappedBy = "materialManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaterialManagementDetail> details = new ArrayList<>();

    @DiffIgnore
    @OneToMany(mappedBy = "materialManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaterialManagementFile> files = new ArrayList<>();

    @DiffIgnore
    @OneToMany(mappedBy = "materialManagement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaterialManagementChangeHistory> changeHistories = new ArrayList<>();

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
    private String inputTypeName;

    @Transient
    @DiffInclude
    private String deliveryDateFormat;

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        this.siteName = this.site != null ? this.site.getName() : null;
        this.processName = this.siteProcess != null ? this.siteProcess.getName() : null;
        this.outsourcingCompanyName = this.outsourcingCompany != null ? this.outsourcingCompany.getName() : null;
        this.inputTypeName = this.inputType != null ? this.inputType.getLabel() : null;
        this.deliveryDateFormat = this.deliveryDate != null
                ? DateTimeFormatUtils.formatKoreaLocalDate(this.deliveryDate)
                : null;
    }

    public void updateFrom(MaterialManagementUpdateRequest request, Site site, SiteProcess siteProcess,
            OutsourcingCompany outsourcingCompany) {
        this.site = site;
        this.siteProcess = siteProcess;
        this.outsourcingCompany = outsourcingCompany;

        Optional.ofNullable(request.inputType())
                .ifPresent(value -> this.inputType = value);
        Optional.ofNullable(request.inputTypeDescription())
                .ifPresent(value -> this.inputTypeDescription = value);
        Optional.ofNullable(request.deliveryDate())
                .ifPresent(value -> this.deliveryDate = DateTimeFormatUtils.toOffsetDateTime(value));
        Optional.ofNullable(request.memo())
                .ifPresent(value -> this.memo = value);

        syncTransientFields();
    }

    public void changeOutsourcingCompany(OutsourcingCompany outsourcingCompany) {
        this.outsourcingCompany = outsourcingCompany;
    }
}
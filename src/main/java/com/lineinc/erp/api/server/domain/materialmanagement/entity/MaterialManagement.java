package com.lineinc.erp.api.server.domain.materialmanagement.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementInputType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementUpdateRequest;
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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(indexes = {
        @Index(columnList = "deliveryDate"),
        @Index(columnList = "createdAt")
})
public class MaterialManagement extends BaseEntity {

    private static final String SEQUENCE_NAME = "material_management_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_ID)
    private Site site;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_PROCESS_ID)
    private SiteProcess siteProcess;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    private OutsourcingCompany outsourcingCompany;

    /**
     * 공제업체
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DEDUCTION_COMPANY_ID)
    private OutsourcingCompany deductionCompany;

    /**
     * 공제업체계약
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DEDUCTION_COMPANY_CONTRACT_ID)
    private OutsourcingCompanyContract deductionCompanyContract;

    @DiffIgnore
    @Column
    @Enumerated(EnumType.STRING)
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
    @DiffIgnore
    @Column(nullable = false)
    private OffsetDateTime deliveryDate;

    /**
     * 비고
     */
    @DiffInclude
    @Column
    private String memo;

    @DiffIgnore
    @OneToMany(mappedBy = AppConstants.MATERIAL_MANAGEMENT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaterialManagementDetail> details = new ArrayList<>();

    @DiffIgnore
    @OneToMany(mappedBy = AppConstants.MATERIAL_MANAGEMENT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaterialManagementFile> files = new ArrayList<>();

    @DiffIgnore
    @OneToMany(mappedBy = AppConstants.MATERIAL_MANAGEMENT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
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

    @Transient
    @DiffInclude
    private String deductionCompanyName;

    @Transient
    @DiffInclude
    private String deductionCompanyContractName;

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
        this.deductionCompanyName = this.deductionCompany != null ? this.deductionCompany.getName() : null;
        this.deductionCompanyContractName = this.deductionCompanyContract != null
                ? this.deductionCompanyContract.getContractName()
                : null;
    }

    public void updateFrom(final MaterialManagementUpdateRequest request, final Site site,
            final SiteProcess siteProcess,
            final OutsourcingCompany outsourcingCompany, final OutsourcingCompany deductionCompany,
            final OutsourcingCompanyContract deductionCompanyContract) {
        this.site = site;
        this.siteProcess = siteProcess;
        this.outsourcingCompany = outsourcingCompany;
        this.deductionCompany = deductionCompany;
        this.deductionCompanyContract = deductionCompanyContract;
        this.inputTypeDescription = request.inputTypeDescription();
        this.deliveryDate = DateTimeFormatUtils.toOffsetDateTime(request.deliveryDate());
        this.memo = request.memo();
        this.inputType = request.inputType();

        syncTransientFields();
    }

    public void changeOutsourcingCompany(final OutsourcingCompany outsourcingCompany) {
        this.outsourcingCompany = outsourcingCompany;
    }
}
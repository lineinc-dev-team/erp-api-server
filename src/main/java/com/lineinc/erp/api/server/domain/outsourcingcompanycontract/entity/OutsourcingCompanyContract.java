package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyTaxInvoiceConditionType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        @Index(columnList = "status"),
        @Index(columnList = "type"),
        @Index(columnList = "category"),
        @Index(columnList = "contract_start_date"),
        @Index(columnList = "contract_end_date"),
        @Index(columnList = "created_at")
})
public class OutsourcingCompanyContract extends BaseEntity {

    private static final String SEQUENCE_NAME = "outsourcing_company_contract_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffInclude
    @Column
    private String contractName;

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = AppConstants.SITE_ID)
    private Site site;

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = AppConstants.SITE_PROCESS_ID)
    private SiteProcess siteProcess;

    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    private OutsourcingCompany outsourcingCompany;

    @DiffIgnore
    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContractType type;

    @DiffInclude
    @Column
    private String typeDescription;

    @DiffIgnore
    @Column
    private OffsetDateTime contractStartDate;

    @DiffIgnore
    @Column
    private OffsetDateTime contractEndDate;

    @DiffInclude
    @Column
    private Long contractAmount;

    @DiffIgnore
    @Column
    private String defaultDeductions;

    @DiffInclude
    @Column
    private String defaultDeductionsDescription;

    @DiffIgnore
    @Column
    private OutsourcingCompanyTaxInvoiceConditionType taxInvoiceCondition;

    @DiffInclude
    @Column
    private Integer taxInvoiceIssueDayOfMonth;

    @DiffIgnore
    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContractCategoryType category;

    @DiffInclude
    @Column
    private String workTypeName; // 공종명

    @DiffIgnore
    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContractStatus status;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    // 계약 담당자 목록
    @DiffIgnore
    @OneToMany(mappedBy = AppConstants.OUTSOURCING_COMPANY_CONTRACT_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractContact> contacts = new ArrayList<>();

    // 계약 첨부파일 목록
    @DiffIgnore
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractFile> files = new ArrayList<>();

    // 계약 인력 목록
    @DiffIgnore
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractWorker> workers = new ArrayList<>();

    // 계약 장비 목록
    @DiffIgnore
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractEquipment> equipments = new ArrayList<>();

    // 계약 운전자 목록
    @DiffIgnore
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractDriver> drivers = new ArrayList<>();

    // 계약 공사항목 목록
    @DiffIgnore
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractConstruction> constructions = new ArrayList<>();

    // 계약 공사항목 그룹 목록 (V2)
    @DiffIgnore
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractConstructionGroup> constructionGroups = new ArrayList<>();

    // 계약 변경 히스토리 목록
    @DiffIgnore
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractChangeHistory> changeHistories = new ArrayList<>();

    @Transient
    @DiffInclude
    private String typeName;

    @Transient
    @DiffInclude
    private String categoryName;

    @Transient
    @DiffInclude
    private String statusName;

    @Transient
    @DiffInclude
    private String taxInvoiceConditionName;

    @Transient
    @DiffInclude
    private String defaultDeductionsName;

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
    private String contractStartDateFormat;

    @Transient
    @DiffInclude
    private String contractEndDateFormat;

    /**
     * 외주업체 계약 정보를 수정합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractUpdateRequest request, final Site site,
            final SiteProcess siteProcess,
            final OutsourcingCompany outsourcingCompany) {

        // 현장, 공정, 외주업체 수정
        this.contractName = request.contractName();
        this.site = site;
        this.siteProcess = siteProcess;
        this.outsourcingCompany = outsourcingCompany;
        this.typeDescription = request.typeDescription();
        this.contractStartDate = request.contractStartDate().atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
        this.contractEndDate = request.contractEndDate().atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
        this.contractAmount = request.contractAmount();
        this.defaultDeductions = request.defaultDeductionsType();
        this.defaultDeductionsDescription = request.defaultDeductionsDescription();
        this.taxInvoiceCondition = request.taxInvoiceCondition();
        this.taxInvoiceIssueDayOfMonth = request.taxInvoiceIssueDayOfMonth();
        this.category = request.category();
        this.workTypeName = request.workTypeName();
        this.status = request.status();
        this.memo = request.memo();

        // transient 필드 동기화
        syncTransientFields();
    }

    /**
     * Transient 필드들을 동기화합니다.
     */
    public void syncTransientFields() {
        this.typeName = this.type != null ? this.type.getLabel() : null;
        this.categoryName = this.category != null ? this.category.getLabel() : null;
        this.statusName = this.status != null ? this.status.getLabel() : null;
        this.taxInvoiceConditionName = this.taxInvoiceCondition != null ? this.taxInvoiceCondition.getLabel() : null;
        this.defaultDeductionsName = (this.defaultDeductions == null || this.defaultDeductions.isBlank()) ? null
                : Arrays.stream(this.defaultDeductions.split(","))
                        .map(String::trim)
                        .map(OutsourcingCompanyDefaultDeductionsType::safeLabelOf)
                        .collect(Collectors.joining(","));

        // 관련 엔티티 이름들 동기화
        this.siteName = this.site != null ? this.site.getName() : null;
        this.processName = this.siteProcess != null ? this.siteProcess.getName() : null;
        this.outsourcingCompanyName = this.outsourcingCompany != null ? this.outsourcingCompany.getName() : null;

        // 날짜 포맷 동기화
        this.contractStartDateFormat = this.contractStartDate != null
                ? com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils
                        .formatKoreaLocalDate(this.contractStartDate)
                : null;
        this.contractEndDateFormat = this.contractEndDate != null
                ? com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils.formatKoreaLocalDate(this.contractEndDate)
                : null;
    }

}
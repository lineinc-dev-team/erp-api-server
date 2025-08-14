package com.lineinc.erp.api.server.domain.outsourcing.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyTaxInvoiceConditionType;
import com.lineinc.erp.api.server.domain.outsourcing.repository.OutsourcingCompanyRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.repository.SiteProcessRepository;
import com.lineinc.erp.api.server.domain.site.repository.SiteRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractUpdateRequest;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;

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
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contract_seq")
    @SequenceGenerator(name = "outsourcing_company_contract_seq", sequenceName = "outsourcing_company_contract_seq", allocationSize = 1)
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

    @DiffIgnore
    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContractStatus status;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    // 계약 담당자 목록
    @DiffIgnore
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
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
    public void updateFrom(OutsourcingCompanyContractUpdateRequest request,
            SiteRepository siteRepository,
            SiteProcessRepository siteProcessRepository,
            OutsourcingCompanyRepository outsourcingCompanyRepository) {

        // 현장, 공정, 외주업체 수정
        if (request.siteId() != null) {
            com.lineinc.erp.api.server.domain.site.entity.Site site = siteRepository.findById(request.siteId())
                    .orElseThrow(() -> new IllegalArgumentException(ValidationMessages.SITE_NOT_FOUND));
            this.site = site;
        }

        if (request.processId() != null) {
            com.lineinc.erp.api.server.domain.site.entity.SiteProcess siteProcess = siteProcessRepository
                    .findById(request.processId())
                    .orElseThrow(() -> new IllegalArgumentException(ValidationMessages.SITE_PROCESS_NOT_FOUND));
            this.siteProcess = siteProcess;
        }

        if (request.outsourcingCompanyId() != null) {
            com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany company = outsourcingCompanyRepository
                    .findById(request.outsourcingCompanyId())
                    .orElseThrow(
                            () -> new IllegalArgumentException(ValidationMessages.OUTSOURCING_COMPANY_NOT_FOUND));
            this.outsourcingCompany = company;
        }

        // 기존 필드들 수정
        java.util.Optional.ofNullable(request.type()).ifPresent(val -> this.type = val);
        java.util.Optional.ofNullable(request.typeDescription()).ifPresent(val -> this.typeDescription = val);
        java.util.Optional.ofNullable(request.contractStartDate())
                .ifPresent(val -> this.contractStartDate = val.atStartOfDay().atOffset(java.time.ZoneOffset.UTC));
        java.util.Optional.ofNullable(request.contractEndDate())
                .ifPresent(val -> this.contractEndDate = val.atStartOfDay().atOffset(java.time.ZoneOffset.UTC));
        java.util.Optional.ofNullable(request.contractAmount()).ifPresent(val -> this.contractAmount = val);
        java.util.Optional.ofNullable(request.defaultDeductionsType()).ifPresent(val -> this.defaultDeductions = val);
        java.util.Optional.ofNullable(request.defaultDeductionsDescription())
                .ifPresent(val -> this.defaultDeductionsDescription = val);
        java.util.Optional.ofNullable(request.taxInvoiceCondition()).ifPresent(val -> this.taxInvoiceCondition = val);
        java.util.Optional.ofNullable(request.taxInvoiceIssueDayOfMonth())
                .ifPresent(val -> this.taxInvoiceIssueDayOfMonth = val);
        java.util.Optional.ofNullable(request.category()).ifPresent(val -> this.category = val);
        java.util.Optional.ofNullable(request.status()).ifPresent(val -> this.status = val);
        java.util.Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);

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
                : java.util.Arrays.stream(this.defaultDeductions.split(","))
                        .map(String::trim)
                        .map(com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType::safeLabelOf)
                        .collect(java.util.stream.Collectors.joining(","));

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
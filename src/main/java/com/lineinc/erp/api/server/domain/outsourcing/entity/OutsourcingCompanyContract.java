package com.lineinc.erp.api.server.domain.outsourcing.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyTaxInvoiceConditionType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyContractUpdateRequest;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_process_id")
    private SiteProcess siteProcess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    private OutsourcingCompany outsourcingCompany;

    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContractType type;

    @Column
    private String typeDescription;

    @Column
    private OffsetDateTime contractStartDate;

    @Column
    private OffsetDateTime contractEndDate;

    @Column
    private Long contractAmount;

    @Column
    private String defaultDeductions;

    @Column
    private String defaultDeductionsDescription;

    @Column
    private OutsourcingCompanyTaxInvoiceConditionType taxInvoiceCondition;

    @Column
    private Integer taxInvoiceIssueDayOfMonth;

    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContractCategoryType category;

    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContractStatus status;

    @Column(columnDefinition = "TEXT")
    private String memo;

    // 계약 담당자 목록
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractContact> contacts = new ArrayList<>();

    // 계약 첨부파일 목록
    @OneToMany(mappedBy = "outsourcingCompanyContract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OutsourcingCompanyContractFile> files = new ArrayList<>();

    /**
     * 외주업체 계약 정보를 수정합니다.
     */
    public void updateFrom(OutsourcingCompanyContractUpdateRequest request) {
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
    }
}
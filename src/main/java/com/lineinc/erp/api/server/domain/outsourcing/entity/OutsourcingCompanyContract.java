package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractCategoryType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractType;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyContractStatus;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyTaxInvoiceConditionType;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

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

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    private OutsourcingCompany outsourcingCompany;

    @Column
    private String businessNumber;

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
}
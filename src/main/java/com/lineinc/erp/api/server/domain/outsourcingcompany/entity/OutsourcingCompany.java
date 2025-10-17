package com.lineinc.erp.api.server.domain.outsourcingcompany.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "ceo_name"),
        @Index(columnList = "email"),
        @Index(columnList = "business_number"),
        @Index(columnList = "type"),
        @Index(columnList = "landline_number"),
        @Index(columnList = "created_at"),
})
public class OutsourcingCompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_seq")
    @SequenceGenerator(name = "outsourcing_company_seq", sequenceName = "outsourcing_company_seq", allocationSize = 1)
    private Long id;

    @DiffInclude
    @Column(nullable = false)
    private String name;

    @DiffInclude
    @Column
    private String businessNumber;

    @DiffIgnore
    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyType type;

    @DiffInclude
    @Column
    private String typeDescription;

    @DiffInclude
    @Column
    private String ceoName;

    @DiffInclude
    @Column
    private String address;

    @DiffInclude
    @Column
    private String detailAddress;

    @DiffInclude
    @Column
    private String landlineNumber;

    @DiffInclude
    @Column
    private String phoneNumber;

    @DiffInclude
    @Column
    private String email;

    @DiffInclude
    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    /**
     * 기본공제 항목
     */
    @Setter
    @DiffIgnore
    private String defaultDeductions; // "FOUR_INSURANCE,INCOME_TAX" 형식

    @Setter
    @DiffInclude
    private String defaultDeductionsDescription;

    /**
     * 계좌 정보 - 은행
     */
    @DiffInclude
    @Column(nullable = false)
    private String bankName;

    /**
     * 계좌 정보 - 계좌번호
     */
    @DiffInclude
    @Column(nullable = false)
    private String accountNumber;

    /**
     * 계좌 정보 - 예금주
     */
    @DiffInclude
    @Column(nullable = false)
    private String accountHolder;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @DiffIgnore
    @Builder.Default
    @OneToMany(mappedBy = "outsourcingCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutsourcingCompanyContact> contacts = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(mappedBy = "outsourcingCompany", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutsourcingCompanyFile> files = new ArrayList<>();

    @Transient
    @DiffInclude
    private String typeName;

    @Transient
    @DiffInclude
    private String defaultDeductionsName;

    public void syncTransientFields() {
        this.typeName = this.type != null ? this.type.getLabel() : null;
        this.defaultDeductionsName = (this.defaultDeductions == null || this.defaultDeductions.isBlank()) ? null
                : Arrays.stream(this.defaultDeductions.split(","))
                        .map(String::trim)
                        .map(OutsourcingCompanyDefaultDeductionsType::safeLabelOf)
                        .collect(Collectors.joining(","));
    }

    public void updateFrom(final OutsourcingCompanyUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.businessNumber()).ifPresent(val -> this.businessNumber = val);
        Optional.ofNullable(request.typeDescription()).ifPresent(val -> this.typeDescription = val);
        Optional.ofNullable(request.ceoName()).ifPresent(val -> this.ceoName = val);
        Optional.ofNullable(request.address()).ifPresent(val -> this.address = val);
        Optional.ofNullable(request.detailAddress()).ifPresent(val -> this.detailAddress = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);
        Optional.ofNullable(request.defaultDeductions()).ifPresent(val -> this.defaultDeductions = val);
        Optional.ofNullable(request.defaultDeductionsDescription())
                .ifPresent(val -> this.defaultDeductionsDescription = val);
        Optional.ofNullable(request.bankName()).ifPresent(val -> this.bankName = val);
        Optional.ofNullable(request.accountNumber()).ifPresent(val -> this.accountNumber = val);
        Optional.ofNullable(request.accountHolder()).ifPresent(val -> this.accountHolder = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        syncTransientFields();
    }

    /**
     * 외주업체 기본 정보를 업데이트합니다.
     */
    public void updateOutsourcingCompanyInfo(final String name, final String businessNumber, final String ceoName,
            final String bankName, final String accountNumber, final String accountHolder, final String memo) {
        Optional.ofNullable(name).ifPresent(val -> this.name = val);
        Optional.ofNullable(businessNumber).ifPresent(val -> this.businessNumber = val);
        Optional.ofNullable(ceoName).ifPresent(val -> this.ceoName = val);
        Optional.ofNullable(bankName).ifPresent(val -> this.bankName = val);
        Optional.ofNullable(accountNumber).ifPresent(val -> this.accountNumber = val);
        Optional.ofNullable(accountHolder).ifPresent(val -> this.accountHolder = val);
        Optional.ofNullable(memo).ifPresent(val -> this.memo = val);
        syncTransientFields();
    }
}
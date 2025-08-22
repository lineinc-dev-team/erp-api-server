package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
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
    @DiffIgnore
    @Column
    private String defaultDeductions; // "FOUR_INSURANCE,INCOME_TAX" 형식

    @DiffInclude
    @Column
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

    public void updateFrom(OutsourcingCompanyUpdateRequest request) {
        java.util.Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        java.util.Optional.ofNullable(request.businessNumber()).ifPresent(val -> this.businessNumber = val);
        java.util.Optional.ofNullable(request.type()).ifPresent(val -> this.type = val);
        java.util.Optional.ofNullable(request.typeDescription()).ifPresent(val -> this.typeDescription = val);
        java.util.Optional.ofNullable(request.ceoName()).ifPresent(val -> this.ceoName = val);
        java.util.Optional.ofNullable(request.address()).ifPresent(val -> this.address = val);
        java.util.Optional.ofNullable(request.detailAddress()).ifPresent(val -> this.detailAddress = val);
        java.util.Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        java.util.Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        java.util.Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        java.util.Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);
        java.util.Optional.ofNullable(request.defaultDeductions()).ifPresent(val -> this.defaultDeductions = val);
        java.util.Optional.ofNullable(request.defaultDeductionsDescription())
                .ifPresent(val -> this.defaultDeductionsDescription = val);
        java.util.Optional.ofNullable(request.bankName()).ifPresent(val -> this.bankName = val);
        java.util.Optional.ofNullable(request.accountNumber()).ifPresent(val -> this.accountNumber = val);
        java.util.Optional.ofNullable(request.accountHolder()).ifPresent(val -> this.accountHolder = val);
        java.util.Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        syncTransientFields();
    }
}
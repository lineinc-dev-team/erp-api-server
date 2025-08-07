package com.lineinc.erp.api.server.domain.outsourcing.entity;

import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;

import java.util.ArrayList;
import java.util.List;

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
    @DiffInclude
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

    public void updateFrom(OutsourcingCompanyUpdateRequest request) {
        this.name = request.name();
        this.type = request.type();
        this.typeDescription = request.typeDescription();
        this.ceoName = request.ceoName();
        this.address = request.address();
        this.detailAddress = request.detailAddress();
        this.landlineNumber = request.landlineNumber();
        this.phoneNumber = request.phoneNumber();
        this.email = request.email();
        this.isActive = Boolean.TRUE.equals(request.isActive());
        this.defaultDeductions = request.defaultDeductions();
        this.defaultDeductionsDescription = request.defaultDeductionsDescription();
        this.bankName = request.bankName();
        this.accountNumber = request.accountNumber();
        this.accountHolder = request.accountHolder();
        this.memo = request.memo();
    }
}
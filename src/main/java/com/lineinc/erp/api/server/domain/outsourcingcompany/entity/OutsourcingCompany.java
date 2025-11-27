package com.lineinc.erp.api.server.domain.outsourcingcompany.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;
import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyDefaultDeductionsType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.enums.OutsourcingCompanyVatType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
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
        @Index(columnList = "created_at"),})
public class OutsourcingCompany extends BaseEntity {
    private static final String SEQUENCE_NAME = "outsourcing_company_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME,
            allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
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
    @Column
    private String bankName;

    /**
     * 계좌 정보 - 계좌번호
     */
    @DiffInclude
    @Column
    private String accountNumber;

    /**
     * 계좌 정보 - 예금주
     */
    @DiffInclude
    @Column
    private String accountHolder;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 부가세 타입 (식당 구분인 경우 필수)
     * - NO_VAT: 부가세 없음
     * - VAT_INCLUDED: 부가세 포함
     * - VAT_SEPARATE: 부가세 별도
     */
    @Enumerated(EnumType.STRING)
    @DiffInclude
    private OutsourcingCompanyVatType vatType;

    @DiffIgnore
    @Builder.Default
    @OneToMany(mappedBy = AppConstants.OUTSOURCING_COMPANY_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutsourcingCompanyContact> contacts = new ArrayList<>();

    @DiffIgnore
    @Builder.Default
    @OneToMany(mappedBy = AppConstants.OUTSOURCING_COMPANY_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutsourcingCompanyFile> files = new ArrayList<>();

    @Transient
    @DiffInclude
    private String typeName;

    @Transient
    @DiffInclude
    private String defaultDeductionsName;

    @Transient
    @DiffInclude
    private String vatTypeName;

    public void syncTransientFields() {
        this.typeName = this.type != null ? this.type.getLabel() : null;
        this.defaultDeductionsName = (this.defaultDeductions == null || this.defaultDeductions.isBlank()) ? null
                : Arrays.stream(this.defaultDeductions.split(","))
                        .map(String::trim)
                        .map(OutsourcingCompanyDefaultDeductionsType::safeLabelOf)
                        .collect(Collectors.joining(","));
        this.vatTypeName = this.vatType != null ? this.vatType.getLabel() : null;
    }

    public void updateFrom(
            final OutsourcingCompanyUpdateRequest request) {
        this.name = request.name();
        this.businessNumber = request.businessNumber();
        this.typeDescription = request.typeDescription();
        this.ceoName = request.ceoName();
        this.address = request.address();
        this.detailAddress = request.detailAddress();
        this.landlineNumber = request.landlineNumber();
        this.phoneNumber = request.phoneNumber();
        this.email = request.email();
        this.isActive = request.isActive();
        this.defaultDeductions = request.defaultDeductions();
        this.defaultDeductionsDescription = request.defaultDeductionsDescription();
        this.bankName = request.bankName();
        this.accountNumber = request.accountNumber();
        this.accountHolder = request.accountHolder();
        this.memo = request.memo();
        this.vatType = request.vatType();
        syncTransientFields();
    }

    /**
     * 외주업체 기본 정보를 업데이트합니다.
     */
    public void updateOutsourcingCompanyInfo(
            final String name,
            final String businessNumber,
            final String ceoName,
            final String bankName,
            final String accountNumber,
            final String accountHolder,
            final String memo) {
        this.name = name;
        this.businessNumber = businessNumber;
        this.ceoName = ceoName;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.memo = memo;
        syncTransientFields();
    }
}

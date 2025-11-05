package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContactSubEquipmentType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractSubEquipmentUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class OutsourcingCompanyContractSubEquipment extends BaseEntity {
    private static final String SEQUENCE_NAME = "outsourcing_company_contact_sub_equipment_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @Column
    @Enumerated(EnumType.STRING)
    private OutsourcingCompanyContactSubEquipmentType type; // 구분값

    @DiffInclude
    @Column
    private String description; // 구분값 설명

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_ID, nullable = false)
    private OutsourcingCompanyContractEquipment equipment;

    @DiffInclude
    @Column
    private Long unitPrice;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String taskDescription; // 작업내용

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    @Transient
    @DiffInclude
    private String typeName; // 구분값 라벨

    /**
     * 보조장비 정보를 수정합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractSubEquipmentUpdateRequest request) {
        this.type = request.type();
        this.description = request.description();
        this.unitPrice = request.unitPrice();
        this.taskDescription = request.taskDescription();
        this.memo = request.memo();

        // transient 필드 동기화
        syncTransientFields();
    }

    /**
     * Transient 필드들을 동기화합니다.
     */
    public void syncTransientFields() {
        this.typeName = this.type != null ? this.type.getLabel() : null;
    }
}

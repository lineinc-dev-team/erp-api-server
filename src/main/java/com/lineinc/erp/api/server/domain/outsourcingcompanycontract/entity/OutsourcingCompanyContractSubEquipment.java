package com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContactSubEquipmentType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request.OutsourcingCompanyContractSubEquipmentUpdateRequest;

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
@SQLRestriction("deleted = false")
public class OutsourcingCompanyContractSubEquipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outsourcing_company_contact_sub_equipment_seq")
    @SequenceGenerator(name = "outsourcing_company_contact_sub_equipment_seq", sequenceName = "outsourcing_company_contact_sub_equipment_seq", allocationSize = 1)
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
    @JoinColumn(name = "outsourcing_company_contract_equipment_id", nullable = false)
    private OutsourcingCompanyContractEquipment equipment;

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
        if (request.type() != null) {
            this.type = request.type();
        }
        if (request.description() != null) {
            this.description = request.description();
        }
        if (request.memo() != null) {
            this.memo = request.memo();
        }

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

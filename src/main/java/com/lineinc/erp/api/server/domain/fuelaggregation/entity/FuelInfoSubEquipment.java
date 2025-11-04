package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationUpdateRequest.FuelInfoUpdateRequest.FuelInfoSubEquipmentUpdateRequest;
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
@SQLRestriction("deleted = false")
public class FuelInfoSubEquipment extends BaseEntity {
    private static final String SEQUENCE_NAME = "fuel_info_sub_equipment_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 유류정보 참조
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.FUEL_INFO_ID, nullable = false)
    private FuelInfo fuelInfo;

    /**
     * 서브장비 (외주업체계약 서브장비)
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_SUB_EQUIPMENT_ID)
    private OutsourcingCompanyContractSubEquipment outsourcingCompanyContractSubEquipment;

    /**
     * 유종
     */
    @DiffIgnore
    @Enumerated(EnumType.STRING)
    private FuelInfoFuelType fuelType;

    /**
     * 주유량 (리터)
     */
    @DiffInclude
    private Long fuelAmount;

    /**
     * 비고 (메모)
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Transient
    @DiffInclude
    private String subEquipmentDescription;

    @Transient
    @DiffInclude
    private String fuelTypeName;

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     * EntitySyncUtils에서 사용하기 위한 단일 파라미터 버전
     */
    public void updateFrom(final FuelInfoSubEquipmentUpdateRequest request) {
        // 서브장비 ID로 조회하여 설정
        final OutsourcingCompanyContractSubEquipment subEquipment = request
                .outsourcingCompanyContractSubEquipmentId() != null
                        ? OutsourcingCompanyContractSubEquipment.builder()
                                .id(request.outsourcingCompanyContractSubEquipmentId())
                                .build()
                        : null;

        this.outsourcingCompanyContractSubEquipment = subEquipment;
        this.fuelType = request.fuelType();
        this.fuelAmount = request.fuelAmount();
        this.memo = request.memo();

        syncTransientFields();
    }

    /**
     * 요청 객체와 서브장비 엔티티로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final FuelInfoSubEquipmentUpdateRequest request,
            final OutsourcingCompanyContractSubEquipment subEquipment) {
        this.outsourcingCompanyContractSubEquipment = subEquipment;
        this.fuelType = request.fuelType();
        this.fuelAmount = request.fuelAmount();
        this.memo = request.memo();

        syncTransientFields();
    }

    public void syncTransientFields() {
        this.subEquipmentDescription = this.outsourcingCompanyContractSubEquipment != null
                ? this.outsourcingCompanyContractSubEquipment.getDescription()
                : null;
        this.fuelTypeName = this.fuelType != null ? this.fuelType.getLabel() : null;
    }

}

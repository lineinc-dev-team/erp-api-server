package com.lineinc.erp.api.server.domain.fuelaggregation.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelType;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;

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
public class FuelInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuel_info_seq")
    @SequenceGenerator(name = "fuel_info_seq", sequenceName = "fuel_info_seq", allocationSize = 1)
    private Long id;

    /**
     * 유류집계 참조
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_aggregation_id", nullable = false)
    private FuelAggregation fuelAggregation;

    /**
     * 업체 (외주업체)
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    private OutsourcingCompany outsourcingCompany;

    /**
     * 기사
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private OutsourcingCompanyContractDriver driver;

    /**
     * 장비
     */
    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private OutsourcingCompanyContractEquipment equipment;

    /**
     * 유종
     */
    @DiffInclude
    @Enumerated(EnumType.STRING)
    @Column
    private FuelType fuelType;

    /**
     * 주유량 (리터)
     */
    @DiffInclude
    @Column
    private Long fuelAmount;

    /**
     * 비고 (메모)
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;
}

package com.lineinc.erp.api.server.domain.dailyreport.entity;

import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportFuelUpdateRequest.FuelUpdateInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Builder
@SQLRestriction("deleted = false")
public class DailyReportFuel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_report_fuel_seq")
    @SequenceGenerator(name = "daily_report_fuel_seq", sequenceName = "daily_report_fuel_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_report_id", nullable = false)
    private DailyReport dailyReport; // 출역일보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    private OutsourcingCompany outsourcingCompany; // 외주업체

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_driver_id")
    private OutsourcingCompanyContractDriver outsourcingCompanyContractDriver; // 외주업체계약기사

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_equipment_id")
    private OutsourcingCompanyContractEquipment outsourcingCompanyContractEquipment; // 외주업체계약 장비

    @Column
    private String fuelType; // 유종

    @Column
    private Long fuelAmount; // 주유량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_aggregation_id")
    private FuelAggregation fuelAggregation; // 유류집계

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final FuelUpdateInfo request) {
        Optional.ofNullable(request.fuelType()).ifPresent(val -> this.fuelType = val);
        Optional.ofNullable(request.fuelAmount()).ifPresent(val -> this.fuelAmount = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

    public void setEntities(final OutsourcingCompany outsourcingCompany,
            final OutsourcingCompanyContractDriver outsourcingCompanyContractDriver,
            final OutsourcingCompanyContractEquipment outsourcingCompanyContractEquipment) {
        this.outsourcingCompany = outsourcingCompany;
        this.outsourcingCompanyContractDriver = outsourcingCompanyContractDriver;
        this.outsourcingCompanyContractEquipment = outsourcingCompanyContractEquipment;
    }
}

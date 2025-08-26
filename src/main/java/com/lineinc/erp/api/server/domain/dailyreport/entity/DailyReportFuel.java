package com.lineinc.erp.api.server.domain.dailyreport.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContractEquipment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
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
    @JoinColumn(name = "outsourcing_company_contract_id")
    private OutsourcingCompanyContract outsourcingCompanyContract; // 외주업체계약

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

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고
}

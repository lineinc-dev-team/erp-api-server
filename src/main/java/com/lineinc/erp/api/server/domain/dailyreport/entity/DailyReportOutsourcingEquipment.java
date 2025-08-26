package com.lineinc.erp.api.server.domain.dailyreport.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
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
public class DailyReportOutsourcingEquipment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_report_outsourcing_equipment_seq")
    @SequenceGenerator(name = "daily_report_outsourcing_equipment_seq", sequenceName = "daily_report_outsourcing_equipment_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_report_id", nullable = false)
    private DailyReport dailyReport; // 출역일보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private OutsourcingCompany company; // 외주업체

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_driver_id")
    private OutsourcingCompanyContractDriver outsourcingCompanyContractDriver; // 외주업체계약 기사

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_equipment_id")
    private OutsourcingCompanyContractEquipment outsourcingCompanyContractEquipment; // 외주업체계약 장비

    @Column(columnDefinition = "TEXT")
    private String workContent; // 작업내용

    @Column
    private Long unitPrice; // 단가

    @Column
    private Double workHours; // 시간

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고
}

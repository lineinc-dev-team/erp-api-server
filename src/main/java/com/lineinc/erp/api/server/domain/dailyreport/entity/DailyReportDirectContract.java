package com.lineinc.erp.api.server.domain.dailyreport.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;

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
public class DailyReportDirectContract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_report_direct_contract_seq")
    @SequenceGenerator(name = "daily_report_direct_contract_seq", sequenceName = "daily_report_direct_contract_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_report_id", nullable = false)
    private DailyReport dailyReport; // 출역일보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private OutsourcingCompany company; // 업체

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    private Labor labor; // 노무인력

    @Column
    private String position; // 직급

    @Column(columnDefinition = "TEXT")
    private String workContent; // 작업내용

    @Column
    private Long unitPrice; // 단가

    @Column
    private Double workQuantity; // 공수

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고
}

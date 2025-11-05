package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class DailyReportDirectContractOutsourcing extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_direct_contract_outsourcing_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DAILY_REPORT_ID, nullable = false)
    private DailyReport dailyReport; // 출역일보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    private OutsourcingCompany outsourcingCompany; // 외주업체

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.LABOR_ID)
    private Labor labor; // 노무인력

    private String position; // 직급

    @Column(columnDefinition = "TEXT")
    private String workContent; // 작업내용

    private Long unitPrice; // 단가

    private Double workQuantity; // 공수

    private String fileUrl;

    private String originalFileName;

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

}

package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstruction;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Table(indexes = {
        @Index(columnList = "created_at")
})
public class DailyReportOutsourcingConstruction extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_outsourcing_construction_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DAILY_REPORT_ID, nullable = false)
    private DailyReport dailyReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    private OutsourcingCompany outsourcingCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONSTRUCTION_ID)
    private OutsourcingCompanyContractConstruction outsourcingCompanyContractConstruction;

    private String unit; // 단위

    private Integer quantity; // 수량

    private String contractFileUrl; // 계약서 파일 URL

    private String contractOriginalFileName; // 계약서 원본 파일명

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    public void updateContractFile(final String contractFileUrl, final String contractOriginalFileName) {
        this.contractFileUrl = contractFileUrl;
        this.contractOriginalFileName = contractOriginalFileName;
    }
}

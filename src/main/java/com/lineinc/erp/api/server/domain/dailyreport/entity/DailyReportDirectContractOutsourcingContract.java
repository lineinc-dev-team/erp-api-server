package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportDirectContractOutsourcingContractUpdateRequest.DirectContractOutsourcingContractUpdateInfo;
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
/**
 * 직영/용역 외주 정보
 */
public class DailyReportDirectContractOutsourcingContract extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_direct_contract_outsourcing_contract_seq";

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
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_ID)
    private OutsourcingCompanyContract outsourcingCompanyContract; // 외주업체 계약

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.LABOR_ID)
    private Labor labor; // 노무인력

    private Double workQuantity; // 공수

    private String fileUrl; // 사진 URL

    private String originalFileName; // 사진 원본 파일명

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final DirectContractOutsourcingContractUpdateInfo request,
            final OutsourcingCompanyContract outsourcingCompanyContract,
            final OutsourcingCompany outsourcingCompany,
            final Labor labor) {
        this.outsourcingCompany = outsourcingCompany;
        this.outsourcingCompanyContract = outsourcingCompanyContract;
        this.labor = labor;
        this.workQuantity = request.workQuantity();
        this.originalFileName = request.originalFileName();
        this.fileUrl = request.fileUrl();
        this.memo = request.memo();
    }
}

package com.lineinc.erp.api.server.domain.dailyreport.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingUpdateRequest.OutsourcingUpdateInfo;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.SQLRestriction;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Builder
@SQLRestriction("deleted = false")
public class DailyReportOutsourcing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_report_outsourcing_seq")
    @SequenceGenerator(name = "daily_report_outsourcing_seq", sequenceName = "daily_report_outsourcing_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_report_id", nullable = false)
    private DailyReport dailyReport; // 출역일보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    private OutsourcingCompany outsourcingCompany; // 업체

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_worker_id")
    private OutsourcingCompanyContractWorker outsourcingCompanyContractWorker; // 외주업체계약 인력

    @Column
    private String category; // 구분값

    @Column(columnDefinition = "TEXT")
    private String workContent; // 작업내용

    @Column
    private Double workQuantity; // 공수

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(OutsourcingUpdateInfo request) {
        Optional.ofNullable(request.category()).ifPresent(val -> this.category = val);
        Optional.ofNullable(request.workContent()).ifPresent(val -> this.workContent = val);
        Optional.ofNullable(request.workQuantity()).ifPresent(val -> this.workQuantity = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

    public void setEntities(OutsourcingCompany outsourcingCompany,
            OutsourcingCompanyContractWorker outsourcingCompanyContractWorker) {
        this.outsourcingCompany = outsourcingCompany;
        this.outsourcingCompanyContractWorker = outsourcingCompanyContractWorker;
    }
}

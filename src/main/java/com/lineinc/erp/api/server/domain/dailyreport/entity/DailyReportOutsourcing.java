package com.lineinc.erp.api.server.domain.dailyreport.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorker;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportOutsourcingUpdateRequest.OutsourcingUpdateInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    @Column
    private String fileUrl;

    @Column
    private String originalFileName;

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    @OneToMany(mappedBy = "dailyReportOutsourcing", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyReportOutsourcingFile> files = new ArrayList<>();

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final OutsourcingUpdateInfo request) {
        Optional.ofNullable(request.category()).ifPresent(val -> this.category = val);
        Optional.ofNullable(request.workContent()).ifPresent(val -> this.workContent = val);
        Optional.ofNullable(request.workQuantity()).ifPresent(val -> this.workQuantity = val);
        this.originalFileName = request.originalFileName();
        this.fileUrl = request.fileUrl();
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

    public void setEntities(final OutsourcingCompany outsourcingCompany,
            final OutsourcingCompanyContractWorker outsourcingCompanyContractWorker) {
        this.outsourcingCompany = outsourcingCompany;
        this.outsourcingCompanyContractWorker = outsourcingCompanyContractWorker;
    }
}

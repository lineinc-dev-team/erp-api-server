package com.lineinc.erp.api.server.domain.dailyreport.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEquipmentUpdateRequest.EquipmentUpdateInfo;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEquipmentUpdateRequest.OutsourcingCompanyContractSubEquipmentUpdateInfo;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.EntitySyncUtils;

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
public class DailyReportOutsourcingEquipment extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_outsourcing_equipment_seq";

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
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_DRIVER_ID)
    private OutsourcingCompanyContractDriver outsourcingCompanyContractDriver; // 외주업체계약 기사

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_EQUIPMENT_ID)
    private OutsourcingCompanyContractEquipment outsourcingCompanyContractEquipment; // 외주업체계약 장비

    @Column(columnDefinition = "TEXT")
    private String workContent; // 작업내용

    private Long unitPrice; // 단가

    private Double workHours; // 시간

    private String fileUrl;

    private String originalFileName;

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    @Builder.Default
    @OneToMany(mappedBy = "dailyReportOutsourcingEquipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyReportOutsourcingEquipmentSubEquipment> subEquipments = new ArrayList<>(); // 서브 장비 목록

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final EquipmentUpdateInfo request, final OutsourcingCompany outsourcingCompany,
            final OutsourcingCompanyContractDriver outsourcingCompanyContractDriver,
            final OutsourcingCompanyContractEquipment outsourcingCompanyContractEquipment) {
        this.outsourcingCompany = outsourcingCompany;
        this.outsourcingCompanyContractDriver = outsourcingCompanyContractDriver;
        this.outsourcingCompanyContractEquipment = outsourcingCompanyContractEquipment;
        this.workContent = request.workContent();
        this.unitPrice = request.unitPrice();
        this.workHours = request.workHours();
        this.originalFileName = request.originalFileName();
        this.fileUrl = request.fileUrl();
        this.memo = request.memo();

        // 서브 장비 업데이트
        updateSubEquipments(request.outsourcingCompanyContractSubEquipments());
    }

    /**
     * 서브 장비 목록을 업데이트합니다.
     */
    private void updateSubEquipments(
            final List<OutsourcingCompanyContractSubEquipmentUpdateInfo> subEquipmentRequests) {
        if (subEquipmentRequests == null) {
            return;
        }

        // EntitySyncUtils.syncList를 사용하여 서브 장비 동기화
        EntitySyncUtils.syncList(
                this.subEquipments,
                subEquipmentRequests,
                (final OutsourcingCompanyContractSubEquipmentUpdateInfo dto) -> {
                    return DailyReportOutsourcingEquipmentSubEquipment.builder()
                            .dailyReportOutsourcingEquipment(this)
                            .outsourcingCompanyContractSubEquipment(
                                    OutsourcingCompanyContractSubEquipment.builder()
                                            .id(dto.outsourcingCompanyContractSubEquipmentId())
                                            .build())
                            .workContent(dto.workContent())
                            .unitPrice(dto.unitPrice())
                            .workHours(dto.workHours())
                            .memo(dto.memo())
                            .build();
                });
    }
}

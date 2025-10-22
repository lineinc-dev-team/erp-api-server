package com.lineinc.erp.api.server.domain.dailyreport.entity;

import org.hibernate.annotations.SQLRestriction;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractSubEquipment;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportEquipmentUpdateRequest.OutsourcingCompanyContractSubEquipmentUpdateInfo;
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
public class DailyReportOutsourcingEquipmentSubEquipment extends BaseEntity {
    private static final String SEQUENCE_NAME = "daily_report_outsourcing_equipment_sub_equipment_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_report_outsourcing_equipment_id", nullable = false)
    private DailyReportOutsourcingEquipment dailyReportOutsourcingEquipment; // 출역일보 외주업체계약 장비

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_contract_sub_equipment_id")
    private OutsourcingCompanyContractSubEquipment outsourcingCompanyContractSubEquipment; // 외주업체계약 서브 장비

    @Column(columnDefinition = "TEXT")
    private String workContent; // 작업내용

    private Long unitPrice; // 단가

    private Double workHours; // 시간

    @Column(columnDefinition = "TEXT")
    private String memo; // 비고

    /**
     * 요청 객체로부터 엔티티를 업데이트합니다.
     */
    public void updateFrom(final OutsourcingCompanyContractSubEquipmentUpdateInfo request) {
        this.outsourcingCompanyContractSubEquipment = OutsourcingCompanyContractSubEquipment.builder()
                .id(request.outsourcingCompanyContractSubEquipmentId())
                .build();
        this.workContent = request.workContent();
        this.unitPrice = request.unitPrice();
        this.workHours = request.workHours();
        this.memo = request.memo();
    }

}

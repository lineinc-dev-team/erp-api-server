package com.lineinc.erp.api.server.domain.managementcost.entity;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;
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
import jakarta.persistence.Transient;
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
public class ManagementCostMealFeeDetailEquipment extends BaseEntity {

    private static final String SEQUENCE_NAME = "management_cost_meal_fee_detail_equipment_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.MANAGEMENT_COST_ID, nullable = false)
    @DiffIgnore
    private ManagementCost managementCost;

    /**
     * 외주업체 테이블과 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_ID)
    @DiffIgnore
    private OutsourcingCompany outsourcingCompany;

    /**
     * 기사 테이블과 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.OUTSOURCING_COMPANY_CONTRACT_DRIVER_ID)
    @DiffIgnore
    private OutsourcingCompanyContractDriver outsourcingCompanyContractDriver;

    /**
     * 조식 개수
     */
    @Column
    @DiffInclude
    private Integer breakfastCount;

    /**
     * 중식 개수
     */
    @Column
    @DiffInclude
    private Integer lunchCount;

    /**
     * 석식 개수
     */
    @Column
    @DiffInclude
    private Integer dinnerCount;

    /**
     * 단가
     */
    @Column
    @DiffInclude
    private Long unitPrice;

    /**
     * 금액
     */
    @Column
    @DiffInclude
    private Long amount;

    /**
     * 비고
     */
    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo;

    @Transient
    @DiffInclude
    private String outsourcingCompanyName;

    @Transient
    @DiffInclude
    private String outsourcingCompanyContractDriverName;

    /**
     * Javers 감사 로그를 위한 transient 필드 동기화
     */
    public void syncTransientFields() {
        this.outsourcingCompanyContractDriverName = this.outsourcingCompanyContractDriver != null
                ? this.outsourcingCompanyContractDriver.getName()
                : null;
        this.outsourcingCompanyName = this.outsourcingCompany != null ? this.outsourcingCompany.getName() : null;
    }
}

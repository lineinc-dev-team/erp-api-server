package com.lineinc.erp.api.server.domain.managementcost.entity;

import java.util.Optional;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labor.entity.Labor;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostMealFeeDetailUpdateRequest;
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
@SQLRestriction("deleted = false")
public class ManagementCostMealFeeDetailOutsourcing extends BaseEntity {

    private static final String SEQUENCE_NAME = "management_cost_meal_fee_detail_outsourcing_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_cost_id", nullable = false)
    @DiffIgnore
    private ManagementCost managementCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outsourcing_company_id")
    @DiffIgnore
    private OutsourcingCompany outsourcingCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    @DiffIgnore
    private Labor labor;

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
    private String laborName;

    // ID만 저장할 필드 추가
    @Transient
    private Long laborId;

    /**
     * Javers 감사 로그를 위한 transient 필드 동기화
     */
    public void syncTransientFields() {
        this.laborName = this.labor != null ? this.labor.getName() : null;
    }

    public void updateFrom(final ManagementCostMealFeeDetailUpdateRequest request) {
        this.laborId = request.laborId();

        // 직접 업데이트 가능한 필드들
        Optional.ofNullable(request.breakfastCount()).ifPresent(val -> this.breakfastCount = val);
        Optional.ofNullable(request.lunchCount()).ifPresent(val -> this.lunchCount = val);
        Optional.ofNullable(request.unitPrice()).ifPresent(val -> this.unitPrice = val);
        Optional.ofNullable(request.amount()).ifPresent(val -> this.amount = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

    /**
     * 연관 엔티티를 설정하고 transient 필드를 동기화합니다.
     */
    public void setEntities(final Labor labor) {
        this.labor = labor;
        syncTransientFields();
    }

}

package com.lineinc.erp.api.server.domain.managementcost.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostMealFeeDetailUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.DiffIgnore;
import jakarta.persistence.Transient;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class ManagementCostMealFeeDetail extends BaseEntity
        implements UpdatableFrom<ManagementCostMealFeeDetailUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "management_cost_meal_fee_detail_seq")
    @SequenceGenerator(name = "management_cost_meal_fee_detail_seq", sequenceName = "management_cost_meal_fee_detail_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_cost_id", nullable = false)
    @DiffIgnore
    private ManagementCost managementCost;

    /**
     * 직종
     */
    @Column
    @DiffInclude
    private String workType;

    /**
     * 인력 테이블과 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    @DiffIgnore
    private Labor labor;

    /**
     * 이름
     */
    @Column
    @DiffInclude
    private String name;

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

    public void updateFrom(ManagementCostMealFeeDetailUpdateRequest request) {
        // ID만 저장
        this.laborId = request.laborId();

        // 직접 업데이트 가능한 필드들
        Optional.ofNullable(request.workType()).ifPresent(val -> this.workType = val);
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.breakfastCount()).ifPresent(val -> this.breakfastCount = val);
        Optional.ofNullable(request.lunchCount()).ifPresent(val -> this.lunchCount = val);
        Optional.ofNullable(request.unitPrice()).ifPresent(val -> this.unitPrice = val);
        Optional.ofNullable(request.amount()).ifPresent(val -> this.amount = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

    /**
     * 연관 엔티티를 설정하고 transient 필드를 동기화합니다.
     */
    public void setEntities(Labor labor) {
        this.labor = labor;
        syncTransientFields();
    }

}

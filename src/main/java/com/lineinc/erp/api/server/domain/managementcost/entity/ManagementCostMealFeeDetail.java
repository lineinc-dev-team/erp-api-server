package com.lineinc.erp.api.server.domain.managementcost.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.labormanagement.entity.Labor;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class ManagementCostMealFeeDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "management_cost_meal_fee_detail_seq")
    @SequenceGenerator(name = "management_cost_meal_fee_detail_seq", sequenceName = "management_cost_meal_fee_detail_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_cost_id", nullable = false)
    private ManagementCost managementCost;

    /**
     * 직종
     */
    @Column
    private String workType;

    /**
     * 인력 테이블과 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labor_id")
    private Labor labor;

    /**
     * 이름
     */
    @Column
    private String name;

    /**
     * 조식 개수
     */
    @Column
    private Integer breakfastCount;

    /**
     * 중식 개수
     */
    @Column
    private Integer lunchCount;

    /**
     * 단가
     */
    @Column
    private Long unitPrice;

    /**
     * 금액
     */
    @Column
    private Long amount;

    /**
     * 비고
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

}

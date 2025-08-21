package com.lineinc.erp.api.server.domain.managementcost.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
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
public class ManagementCostKeyMoneyDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "management_cost_key_money_detail_seq")
    @SequenceGenerator(name = "management_cost_key_money_detail_seq", sequenceName = "management_cost_key_money_detail_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_cost_id", nullable = false)
    private ManagementCost managementCost;

    /**
     * 계정
     */
    @Column
    private String account;

    /**
     * 사용목적
     */
    @Column
    private String purpose;

    /**
     * 인원수
     */
    @Column
    private Integer personnelCount;

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

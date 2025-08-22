package com.lineinc.erp.api.server.domain.managementcost.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostKeyMoneyDetailUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class ManagementCostKeyMoneyDetail extends BaseEntity
        implements UpdatableFrom<ManagementCostKeyMoneyDetailUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "management_cost_key_money_detail_seq")
    @SequenceGenerator(name = "management_cost_key_money_detail_seq", sequenceName = "management_cost_key_money_detail_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_cost_id", nullable = false)
    private ManagementCost managementCost;

    /**
     * 계정
     */
    @DiffInclude
    @Column
    private String account;

    /**
     * 사용목적
     */
    @DiffInclude
    @Column
    private String purpose;

    /**
     * 인원수
     */
    @DiffInclude
    @Column
    private Integer personnelCount;

    /**
     * 금액
     */
    @DiffInclude
    @Column
    private Long amount;

    /**
     * 비고
     */
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Override
    public void updateFrom(ManagementCostKeyMoneyDetailUpdateRequest request) {
        Optional.ofNullable(request.account()).ifPresent(val -> this.account = val);
        Optional.ofNullable(request.purpose()).ifPresent(val -> this.purpose = val);
        Optional.ofNullable(request.personnelCount()).ifPresent(val -> this.personnelCount = val);
        Optional.ofNullable(request.amount()).ifPresent(val -> this.amount = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }
}

package com.lineinc.erp.api.server.domain.managementcost.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.common.entity.interfaces.UpdatableFrom;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostDetailUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.util.Optional;

@Entity
@Table(indexes = {
        @Index(columnList = "name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class ManagementCostDetail extends BaseEntity implements UpdatableFrom<ManagementCostDetailUpdateRequest> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "management_cost_detail_seq")
    @SequenceGenerator(name = "management_cost_detail_seq", sequenceName = "management_cost_detail_seq", allocationSize = 1)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_cost_id", nullable = false)
    private ManagementCost managementCost;

    /**
     * 품목 이름
     */
    @DiffInclude
    @Column(nullable = false)
    private String name;

    /**
     * 단가
     */
    @DiffInclude
    @Column(nullable = false)
    private Long unitPrice;

    /**
     * 공급가 (부가세 제외 금액)
     */
    @DiffInclude
    @Column(nullable = false)
    private Long supplyPrice;

    /**
     * 부가세
     */
    @DiffInclude
    @Column(nullable = false)
    private Long vat;

    /**
     * 합계 (공사 가액 + 부가세)
     */
    @DiffInclude
    @Column(nullable = false)
    private Long total;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    @Override
    public void updateFrom(ManagementCostDetailUpdateRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.unitPrice()).ifPresent(val -> this.unitPrice = val);
        Optional.ofNullable(request.supplyPrice()).ifPresent(val -> this.supplyPrice = val);
        Optional.ofNullable(request.vat()).ifPresent(val -> this.vat = val);
        Optional.ofNullable(request.total()).ifPresent(val -> this.total = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

    /**
     * Javers 감사 로그를 위한 transient 필드 동기화
     */
    public void syncTransientFields() {
        // 현재는 추가 transient 필드가 없으므로 비워둠
        // 필요시 여기에 transient 필드 동기화 로직 추가
    }
}
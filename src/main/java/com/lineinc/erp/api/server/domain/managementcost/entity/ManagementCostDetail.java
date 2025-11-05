package com.lineinc.erp.api.server.domain.managementcost.entity;

import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostDetailUpdateRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(indexes = {
        @Index(columnList = "name")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class ManagementCostDetail extends BaseEntity {

    private static final String SEQUENCE_NAME = "management_cost_detail_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @DiffIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.MANAGEMENT_COST_ID, nullable = false)
    private ManagementCost managementCost;

    /**
     * 품목 이름
     */
    @DiffInclude
    @Column(nullable = false)
    private String name;

    /**
     * 수량
     */
    @DiffInclude
    @Column
    private Integer quantity;

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

    /**
     * 공제여부
     */
    @Column
    @DiffInclude
    @Builder.Default
    private Boolean isDeductible = false;

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateFrom(final ManagementCostDetailUpdateRequest request) {
        this.name = request.name();
        this.quantity = request.quantity();
        this.unitPrice = request.unitPrice();
        this.supplyPrice = request.supplyPrice();
        this.vat = request.vat();
        this.total = request.total();
        this.isDeductible = request.isDeductible();
        this.memo = request.memo();
    }

    /**
     * Javers 감사 로그를 위한 transient 필드 동기화
     */
    public void syncTransientFields() {
        // 현재는 추가 transient 필드가 없으므로 비워둠
        // 필요시 여기에 transient 필드 동기화 로직 추가
    }
}
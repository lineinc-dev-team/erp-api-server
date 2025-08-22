package com.lineinc.erp.api.server.domain.managementcost.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class ManagementCostChangeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "management_cost_change_history_seq")
    @SequenceGenerator(name = "management_cost_change_history_seq", sequenceName = "management_cost_change_history_seq", allocationSize = 1)
    private Long id;

    /**
     * 관리비 참조
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_cost_id")
    private ManagementCost managementCost;

    /**
     * 변경 유형
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ManagementCostChangeType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String changes;

    /**
     * 메모
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 메모를 설정합니다.
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }
}

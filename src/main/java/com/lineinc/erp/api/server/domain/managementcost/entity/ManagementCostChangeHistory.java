package com.lineinc.erp.api.server.domain.managementcost.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostChangeHistoryType;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
    private static final String SEQUENCE_NAME = "management_cost_change_history_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    /**
     * 관리비 참조
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.MANAGEMENT_COST_ID)
    private ManagementCost managementCost;

    /**
     * 변경 유형
     */
    @Enumerated(EnumType.STRING)
    @Column
    private ManagementCostChangeHistoryType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String changes;

    @Column
    private String description;

    /**
     * 메모
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.USER_ID)
    private User user;

    /**
     * 메모를 설정합니다.
     */
    public void setMemo(final String memo) {
        this.memo = memo;
    }
}

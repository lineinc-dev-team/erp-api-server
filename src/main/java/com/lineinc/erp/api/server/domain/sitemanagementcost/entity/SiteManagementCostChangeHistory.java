package com.lineinc.erp.api.server.domain.sitemanagementcost.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.sitemanagementcost.enums.SiteManagementCostChangeHistoryType;
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

/**
 * 월별 현장관리비 변경 이력
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class SiteManagementCostChangeHistory extends BaseEntity {

    private static final String SEQUENCE_NAME = "site_management_cost_change_history_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.SITE_MANAGEMENT_COST_ID)
    private SiteManagementCost siteManagementCost;

    @Enumerated(EnumType.STRING)
    private SiteManagementCostChangeHistoryType type;

    /**
     * 변경 내용 (JSON 형식)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String changes;

    /**
     * 변경 설명
     */
    @Column
    private String description;

    /**
     * 메모
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 변경한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 메모를 설정합니다.
     */
    public void setMemo(final String memo) {
        this.memo = memo;
    }
}

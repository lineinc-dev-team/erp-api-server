package com.lineinc.erp.api.server.domain.user.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.user.enums.UserChangeHistoryType;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(indexes = {
        @Index(columnList = "created_at"),
        @Index(columnList = "updated_at")
})
@Getter
@NoArgsConstructor
@SuperBuilder
public class UserChangeHistory extends BaseEntity {

    private static final String SEQUENCE_NAME = "user_change_history_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.USER_ID)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column
    private UserChangeHistoryType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String changes;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.UPDATED_BY_USER_ID)
    private User updatedByUser;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String memo;
}

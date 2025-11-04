package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.role.entity.Role;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(indexes = {
        @Index(columnList = "user_id,role_id"), // 사용자별 역할 조회 최적화
})
@Getter
@NoArgsConstructor
@SuperBuilder
public class UserRole extends BaseEntity {
    private static final String SEQUENCE_NAME = "users_roles_seq";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = AppConstants.USER_ID, nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = AppConstants.ROLE_ID, nullable = false)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String memo;
}

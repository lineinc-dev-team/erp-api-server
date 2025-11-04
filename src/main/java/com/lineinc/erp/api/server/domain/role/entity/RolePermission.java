package com.lineinc.erp.api.server.domain.role.entity;

import java.io.Serial;
import java.io.Serializable;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "role_permissions", indexes = {
        @Index(columnList = "role_id,permission_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class RolePermission extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final String SEQUENCE_NAME = "role_permission_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    // Role과 연관관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = AppConstants.ROLE_ID, nullable = false)
    private Role role;

    // Permission과 연관관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = AppConstants.PERMISSION_ID, nullable = false)
    private Permission permission;

    @Column(columnDefinition = "TEXT")
    private String memo;
}
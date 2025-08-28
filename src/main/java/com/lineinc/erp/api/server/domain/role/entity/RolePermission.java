package com.lineinc.erp.api.server.domain.role.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "role_permissions", indexes = {
        @Index(columnList = "role_id,permission_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class RolePermission extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_permission_seq")
    @SequenceGenerator(name = "role_permission_seq", sequenceName = "role_permission_seq", allocationSize = 1)
    private Long id;

    // Role과 연관관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Permission과 연관관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(columnDefinition = "TEXT")
    private String memo;
}
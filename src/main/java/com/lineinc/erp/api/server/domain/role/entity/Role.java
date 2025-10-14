package com.lineinc.erp.api.server.domain.role.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.UpdateRolesRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@Table(name = "roles", indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "created_at")
})
public class Role extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name = "role_seq", sequenceName = "role_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermission> permissions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoleSiteProcess> siteProcesses = new HashSet<>();

    /**
     * 전체 현장 및 공정 접근 권한
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean hasGlobalSiteProcessAccess = false;

    /**
     * 마스킹 해제 권한
     */
    @Builder.Default
    private Boolean hasUnmaskPermission = false;

    @Column(columnDefinition = "TEXT")
    private String memo;

    public void updateFrom(final UpdateRolesRequest request) {
        Optional.ofNullable(request.name()).ifPresent(val -> this.name = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        Optional.ofNullable(request.hasGlobalSiteProcessAccess())
                .ifPresent(val -> this.hasGlobalSiteProcessAccess = val);
        Optional.ofNullable(request.hasUnmaskPermission())
                .ifPresent(val -> this.hasUnmaskPermission = val);
    }
}
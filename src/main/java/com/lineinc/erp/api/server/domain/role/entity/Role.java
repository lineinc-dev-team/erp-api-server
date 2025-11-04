package com.lineinc.erp.api.server.domain.role.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.UpdateRolesRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
    private static final String SEQUENCE_NAME = "role_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.ROLE_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermission> permissions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.ROLE_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.ROLE_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
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
        this.name = request.name();
        this.memo = request.memo();
        this.hasGlobalSiteProcessAccess = request.hasGlobalSiteProcessAccess();
        this.hasUnmaskPermission = request.hasUnmaskPermission();
    }
}
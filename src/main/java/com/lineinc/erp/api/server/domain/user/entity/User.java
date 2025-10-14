package com.lineinc.erp.api.server.domain.user.entity;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.UpdateUserRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users", indexes = {
        @Index(columnList = "login_id"),
        @Index(columnList = "username"),
        @Index(columnList = "email"),
        @Index(columnList = "created_at"),
        @Index(columnList = "updated_at"),
        @Index(columnList = "last_login_at")
})
@Getter
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    // ===== 기본 계정 정보 (변경 추적 대상) =====
    @Column
    @DiffInclude
    private String username;

    @Column
    @DiffInclude
    private String email;

    @Column
    @DiffInclude
    private String landlineNumber;

    @Column
    @DiffInclude
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    @DiffInclude
    private String memo;

    @Builder.Default
    @Column(nullable = false)
    @DiffInclude
    private boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    @DiffInclude
    private boolean isHeadOffice = false;

    // ===== 보안/시스템 정보 (변경 추적 제외) =====
    @Column(nullable = false)
    @DiffIgnore
    private String loginId;

    @Column
    @Setter
    @DiffIgnore
    private String passwordHash;

    @Column
    @Setter
    @DiffIgnore
    private OffsetDateTime lastLoginAt;

    @Builder.Default
    @Column(nullable = false)
    @Setter
    private boolean requirePasswordReset = true;

    // ===== 조직 정보 (변경 추적 제외) =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @DiffIgnore
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    @DiffIgnore
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    @DiffIgnore
    private Position position;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @DiffIgnore
    private Set<UserRole> userRoles = new HashSet<>();

    // ===== 화면 표시용 필드 (변경 추적 대상) =====
    @Transient
    @DiffInclude
    private String departmentName;

    @Transient
    @DiffInclude
    private String gradeName;

    @Transient
    @DiffInclude
    private String positionName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    public void updatePassword(final String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.requirePasswordReset = false;
    }

    public void resetPassword(final String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.requirePasswordReset = true;
    }

    public void markAsDeletedWithRoles() {
        this.markAsDeleted();
        if (this.userRoles != null) {
            this.userRoles.forEach(UserRole::markAsDeleted);
        }
    }

    public boolean hasRoles() {
        return this.userRoles != null && !this.userRoles.isEmpty();
    }

    public boolean hasGlobalSiteProcessAccess() {
        if (!hasRoles()) {
            return false;
        }
        return this.userRoles.stream()
                .anyMatch(role -> role.getRole().getHasGlobalSiteProcessAccess());
    }

    public void syncTransientFields() {
        this.departmentName = Optional.ofNullable(this.department)
                .map(Department::getName)
                .orElse(null);
        this.gradeName = Optional.ofNullable(this.grade)
                .map(Grade::getName)
                .orElse(null);
        this.positionName = Optional.ofNullable(this.position)
                .map(Position::getName)
                .orElse(null);
    }

    // ===== 업데이트 메서드 =====
    public void updateFrom(final UpdateUserRequest request, final Department department, final Grade grade) {
        Optional.ofNullable(request.username()).ifPresent(val -> this.username = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);
        Optional.ofNullable(request.isHeadOffice()).ifPresent(val -> this.isHeadOffice = val);
        Optional.ofNullable(department).ifPresent(val -> this.department = val);
        Optional.ofNullable(grade).ifPresent(val -> this.grade = val);
        syncTransientFields();
    }

}
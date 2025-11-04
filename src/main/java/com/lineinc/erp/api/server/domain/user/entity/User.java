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
import com.lineinc.erp.api.server.shared.constant.AppConstants;

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
    static final String SEQUENCE_NAME = "user_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = AppConstants.SEQUENCE_ALLOCATION_DEFAULT)
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.DEPARTMENT_ID)
    @DiffIgnore
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.GRADE_ID)
    @DiffIgnore
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = AppConstants.POSITION_ID)
    @DiffIgnore
    private Position position;

    @Builder.Default
    @OneToMany(mappedBy = AppConstants.USER_MAPPED_BY, cascade = CascadeType.ALL, orphanRemoval = true)
    @DiffIgnore
    private Set<UserRole> userRoles = new HashSet<>();

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

    public void updateFrom(final UpdateUserRequest request, final Department department, final Grade grade) {
        this.username = request.username();
        this.email = request.email();
        this.landlineNumber = request.landlineNumber();
        this.phoneNumber = request.phoneNumber();
        this.memo = request.memo();
        this.isActive = request.isActive();
        this.isHeadOffice = request.isHeadOffice();
        this.department = department;
        this.grade = grade;
        syncTransientFields();
    }

    /**
     * 수정이력용 필드
     */

    @Transient
    @DiffInclude
    private String departmentName;

    @Transient
    @DiffInclude
    private String gradeName;

    @Transient
    @DiffInclude
    private String positionName;

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

}
package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.UpdateUserRequest;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.*;

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

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.requirePasswordReset = false;
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
    public void updateFrom(UpdateUserRequest request, Department department, Grade grade, Position position) {
        Optional.ofNullable(request.username()).ifPresent(val -> this.username = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);
        Optional.ofNullable(request.isHeadOffice()).ifPresent(val -> this.isHeadOffice = val);
        Optional.ofNullable(department).ifPresent(val -> this.department = val);
        Optional.ofNullable(grade).ifPresent(val -> this.grade = val);
        Optional.ofNullable(position).ifPresent(val -> this.position = val);
        syncTransientFields();
    }

}
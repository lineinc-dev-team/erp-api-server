package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.user.UpdateUserRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@SuperBuilder
public class User extends BaseEntity implements UserDetails {

    private static final long serialVersionUID = 1L;

    // ===== 기본 정보 =====
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column
    @DiffInclude
    private String username;

    @Column
    private String passwordHash;

    @Column(nullable = false)
    @DiffInclude
    private String email;

    // ===== 연락처 정보 =====
    @Column
    @DiffInclude
    private String landlineNumber;

    @Column
    @DiffInclude
    private String phoneNumber;

    // ===== 조직 정보 =====
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

    // ===== 상태 및 설정 =====
    @Column
    private OffsetDateTime lastLoginAt;

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private boolean requirePasswordReset = true;

    @Builder.Default
    @DiffInclude
    @Column(nullable = false)
    private boolean isActive = true;

    // ===== 연관 관계 =====
    @Builder.Default
    @DiffIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    // ===== 메모 =====
    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    // ===== Transient 필드 (화면 표시용) =====
    @Transient
    @DiffInclude
    private String departmentName;

    @Transient
    @DiffInclude
    private String gradeName;

    @Transient
    @DiffInclude
    private String positionName;

    // ===== UserDetails 구현 =====
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.loginId;
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

    // ===== 비즈니스 메서드 =====
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = OffsetDateTime.now();
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

    public void updateFrom(UpdateUserRequest request,
            PasswordEncoder passwordEncoder,
            DepartmentRepository departmentRepository,
            GradeRepository gradeRepository,
            PositionRepository positionRepository) {

        updateBasicInfo(request);
        updateContactInfo(request);
        updateOrganizationInfo(request, departmentRepository, gradeRepository, positionRepository);
        updatePasswordIfProvided(request, passwordEncoder);
        syncTransientFields();
    }

    // ===== Private 메서드 =====
    private void updateBasicInfo(UpdateUserRequest request) {
        Optional.ofNullable(request.username()).ifPresent(val -> this.username = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
    }

    private void updateContactInfo(UpdateUserRequest request) {
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
    }

    private void updateOrganizationInfo(UpdateUserRequest request,
            DepartmentRepository departmentRepository,
            GradeRepository gradeRepository,
            PositionRepository positionRepository) {
        Optional.ofNullable(request.departmentId())
                .flatMap(departmentRepository::findById)
                .ifPresent(entity -> this.department = entity);

        Optional.ofNullable(request.gradeId())
                .flatMap(gradeRepository::findById)
                .ifPresent(entity -> this.grade = entity);

        Optional.ofNullable(request.positionId())
                .flatMap(positionRepository::findById)
                .ifPresent(entity -> this.position = entity);
    }

    private void updatePasswordIfProvided(UpdateUserRequest request, PasswordEncoder passwordEncoder) {
        if (request.password() != null && !request.password().isBlank()) {
            this.updatePassword(passwordEncoder.encode(request.password()));
        }
    }
}
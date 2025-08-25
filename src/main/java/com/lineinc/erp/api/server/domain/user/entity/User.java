package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.user.UpdateUserRequest;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
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

    private static final long serialVersionUID = 1;

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

    @Column
    @DiffInclude
    private String landlineNumber;

    @Column
    @DiffInclude
    private String phoneNumber;

    @Column(nullable = false)
    @DiffInclude
    private String email;

    @Column
    private OffsetDateTime lastLoginAt;

    @Builder.Default
    @DiffIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private boolean requirePasswordReset = true;

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

    @DiffInclude
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 사용 여부
     */
    @Builder.Default
    @DiffInclude
    @Column(nullable = false)
    private boolean isActive = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * 인증에 사용할 사용자명 반환
     */
    @Override
    public String getUsername() {
        return this.loginId;
    }

    /**
     * 인증에 사용할 비밀번호 반환
     */
    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    /**
     * 비밀번호 수정
     *
     * @param newPasswordHash 새롭게 수정할 비밀번호 해시값
     */
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    /**
     * 최종 로그인 시각 수정
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = OffsetDateTime.now();
    }

    @Transient
    @DiffInclude
    private String departmentName;

    @Transient
    @DiffInclude
    private String gradeName;

    @Transient
    @DiffInclude
    private String positionName;

    /**
     * 연관 엔티티에서 이름 값을 복사해 transient 필드에 세팅
     */
    public void syncTransientFields() {
        this.departmentName = this.department != null ? this.department.getName() : null;
        this.gradeName = this.grade != null ? this.grade.getName() : null;
        this.positionName = this.position != null ? this.position.getName() : null;
    }

    /**
     * UpdateUserRequest DTO로부터 사용자 정보를 업데이트합니다.
     */
    public void updateFrom(UpdateUserRequest request,
            PasswordEncoder passwordEncoder,
            DepartmentRepository departmentRepository,
            GradeRepository gradeRepository,
            PositionRepository positionRepository) {
        Optional.ofNullable(request.username()).ifPresent(val -> this.username = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);
        Optional.ofNullable(request.memo()).ifPresent(val -> this.memo = val);
        Optional.ofNullable(request.departmentId())
                .flatMap(departmentRepository::findById)
                .ifPresent(entity -> {
                    this.department = entity;
                });
        Optional.ofNullable(request.gradeId())
                .flatMap(gradeRepository::findById)
                .ifPresent(entity -> {
                    this.grade = entity;
                });

        Optional.ofNullable(request.positionId())
                .flatMap(positionRepository::findById)
                .ifPresent(entity -> {
                    this.position = entity;
                });
        if (request.password() != null && !request.password().isBlank()) {
            this.updatePassword(passwordEncoder.encode(request.password()));
        }

        syncTransientFields();
    }
}
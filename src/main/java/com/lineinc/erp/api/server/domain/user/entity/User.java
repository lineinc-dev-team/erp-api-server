package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UpdateUserRequest;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SQLRestriction("deleted = false")
public class User extends BaseEntity implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
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
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @Setter
    @Builder.Default
    @Column(nullable = false)
    private boolean requirePasswordReset = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
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
     * UpdateUserRequest DTO로부터 사용자 정보를 업데이트합니다.
     */
    public UserUpdateResult updateFrom(UpdateUserRequest request,
                                       PasswordEncoder passwordEncoder,
                                       DepartmentRepository departmentRepository,
                                       GradeRepository gradeRepository,
                                       PositionRepository positionRepository) {

        User before = User.builder()
                .id(this.id)
                .username(this.username)
                .landlineNumber(this.landlineNumber)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .memo(this.memo)
                .isActive(this.isActive)
                .build();

        before.departmentName = this.department != null ? this.department.getName() : null;
        before.gradeName = this.grade != null ? this.grade.getName() : null;
        before.positionName = this.position != null ? this.position.getName() : null;

        // 기본 필드 업데이트
        Optional.ofNullable(request.username()).ifPresent(val -> this.username = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);

        // 조직 관련 필드 업데이트
        Optional.ofNullable(request.departmentId())
                .flatMap(departmentRepository::findById)
                .ifPresent(entity -> {
                    this.department = entity;
                    this.departmentName = entity.getName();
                });

        Optional.ofNullable(request.gradeId())
                .flatMap(gradeRepository::findById)
                .ifPresent(entity -> {
                    this.grade = entity;
                    this.gradeName = entity.getName();
                });

        Optional.ofNullable(request.positionId())
                .flatMap(positionRepository::findById)
                .ifPresent(entity -> {
                    this.position = entity;
                    this.positionName = entity.getName();
                });

        // 비밀번호 업데이트
        if (request.password() != null && !request.password().isBlank()) {
            this.updatePassword(passwordEncoder.encode(request.password()));
        }

        return new UserUpdateResult(before, this);
    }

    public record UserUpdateResult(User before, User after) {
    }
}
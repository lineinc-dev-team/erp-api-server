package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UpdateUserRequest;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
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
    private String username;

    @Column
    private String passwordHash;

    @Column
    private String landlineNumber;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private String email;

    @Column
    private OffsetDateTime lastLoginAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 사용 여부
     */
    @Builder.Default
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

    /**
     * UpdateUserRequest DTO로부터 사용자 정보를 업데이트합니다.
     *
     * @param request         사용자 수정 요청 정보
     * @param passwordEncoder 비밀번호 암호화에 사용할 인코더
     */
    public void updateFrom(UpdateUserRequest request, PasswordEncoder passwordEncoder) {
        Optional.ofNullable(request.username()).ifPresent(val -> this.username = val);
        Optional.ofNullable(request.email()).ifPresent(val -> this.email = val);
        Optional.ofNullable(request.phoneNumber()).ifPresent(val -> this.phoneNumber = val);
        Optional.ofNullable(request.landlineNumber()).ifPresent(val -> this.landlineNumber = val);
        Optional.ofNullable(request.isActive()).ifPresent(val -> this.isActive = val);

        if (request.departmentId() != null) {
            this.department = Department.builder().id(request.departmentId()).build();
        }
        if (request.gradeId() != null) {
            this.grade = Grade.builder().id(request.gradeId()).build();
        }
        if (request.positionId() != null) {
            this.position = Position.builder().id(request.positionId()).build();
        }

        if (request.password() != null && !request.password().isBlank()) {
            this.updatePassword(passwordEncoder.encode(request.password()));
        }
    }
}
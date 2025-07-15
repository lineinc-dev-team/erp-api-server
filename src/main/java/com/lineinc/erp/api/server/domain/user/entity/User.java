package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UpdateUserRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(columnList = "username")  // username 컬럼에 인덱스 생성 (검색 성능 향상 목적)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
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

    /**
     * 비밀번호 초기화 시각 (비밀번호 변경 혹은 초기화된 시간)
     */
    @Column()
    private OffsetDateTime passwordResetAt;

    @Column()
    private OffsetDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles;

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
        this.passwordResetAt = OffsetDateTime.now(); // 비밀번호 변경 시각도 기록
    }

    /**
     * 최종 로그인 시각 수정
     *
     * @param now 현재 시각
     */
    public void updateLastLoginAt(OffsetDateTime now) {
        this.lastLoginAt = now;
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

        if (request.password() != null && !request.password().isBlank()) {
            this.updatePassword(passwordEncoder.encode(request.password()));
        }
    }
}
package com.lineinc.erp.api.server.domain.user.entity;

import com.lineinc.erp.api.server.domain.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.company.entity.Company;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 소속 회사 정보 (Users - Company : 다대일 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Company company;

    @Column(unique = true, nullable = false)
    private String loginId;

    @Column
    private String username;

    @Column
    private String passwordHash;

    @Column
    private String phoneNumber;

    /**
     * 비밀번호 초기화 시각 (비밀번호 변경 혹은 초기화된 시간)
     */
    @Column()
    private OffsetDateTime passwordResetAt;

    /**
     * 최종 로그인 시각
     */
    @Column()
    private OffsetDateTime lastLoginAt;

    /**
     * 권환 목록 반환
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles;

    /**
     * 권한 목록 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
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
}
package com.lineinc.erp.api.server.domain.users;

import com.lineinc.erp.api.server.common.entity.BaseEntity;
import com.lineinc.erp.api.server.domain.company.Company;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "username")  // username 컬럼에 인덱스 생성 (검색 성능 향상 목적)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users extends BaseEntity implements UserDetails {

    /**
     * 소속 회사 정보 (User - Company : 다대일 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Company company;

    @Column(unique = true, nullable = false)
    private String loginId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column
    private String username;

    @Column
    private String passwordHash;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * 계정 잠금 시각 (로그인 실패 등으로 잠긴 시간 저장)
     */
    @Column
    private LocalDateTime lockedAt;

    /**
     * 비밀번호 초기화 시각 (비밀번호 변경 혹은 초기화된 시간)
     */
    @Column
    private LocalDateTime passwordResetAt;

    /**
     * 최종 로그인 시각
     */
    @Column
    private LocalDateTime lastLoginAt;

    /**
     * 권한 목록 반환 (현재는 비워둠)
     */
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
        this.passwordResetAt = LocalDateTime.now(); // 비밀번호 변경 시각도 기록
    }

    /**
     * 최종 로그인 시각 수정
     *
     * @param now 현재 시각
     */
    public void updateLastLoginAt(LocalDateTime now) {
        this.lastLoginAt = now;
    }
}
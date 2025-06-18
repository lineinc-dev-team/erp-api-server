package com.lineinc.erp.api.server.domain.auth;

import com.lineinc.erp.api.server.common.BaseEntity;
import com.lineinc.erp.api.server.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    /**
     * 연관 유저 정보 (1:1 관계)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * 리프레시 토큰을 생성합니다.
     *
     * @param user       토큰을 소유한 사용자
     * @param token      발급된 리프레시 토큰 문자열
     * @param expiryDate 토큰 만료 시각
     */
    public RefreshToken(User user, String token, LocalDateTime expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    /**
     * 기존 리프레시 토큰 값을 새 값으로 갱신합니다.
     *
     * @param newToken      새로운 리프레시 토큰 문자열
     * @param newExpiryDate 새로운 만료 시각
     */
    public void update(String newToken, LocalDateTime newExpiryDate) {
        this.token = newToken;
        this.expiryDate = newExpiryDate;
    }

    /**
     * 토큰의 만료 여부를 확인합니다.
     *
     * @return 현재 시각이 만료 시각 이후면 true (만료됨), 아니면 false
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
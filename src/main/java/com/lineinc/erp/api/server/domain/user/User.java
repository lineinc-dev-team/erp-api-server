package com.lineinc.erp.api.server.domain.user;

import com.lineinc.erp.api.server.common.BaseEntity;
import com.lineinc.erp.api.server.domain.company.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(columnList = "username")  // username 컬럼에 인덱스 생성 (검색 성능 향상 목적)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

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
}
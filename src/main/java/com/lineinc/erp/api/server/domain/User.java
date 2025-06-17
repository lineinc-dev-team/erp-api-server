package com.lineinc.erp.api.server.domain;

import com.lineinc.erp.api.server.domain.common.BaseEntity;
import com.lineinc.erp.api.server.domain.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(columnList = "username")  // username 컬럼에 인덱스 생성 (검색 성능 향상 목적)
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /**
     * 소속 회사 정보 (User - Company : 다대일 관계)
     * LAZY 로딩 적용 - User 조회 시 Company 정보는 즉시 로딩하지 않고 실제 접근할 때 로딩됨
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)  // 외래키 제약조건, null 불가
    private Company company;

    /**
     * 로그인 ID (중복 불가, null 불가)
     */
    @Column(unique = true, nullable = false)
    private String loginId;

    /**
     * 계정 유형 (관리자, 현장 담당자, 외주사 등)
     * Enum 타입으로 DB에 문자열(String)로 저장됨
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    /**
     * 사용자 이름 (실명)
     */
    @Column()
    private String username;

    /**
     * 비밀번호 해시 값
     */
    @Column()
    private String passwordHash;

    /**
     * 휴대폰 번호 (비밀번호 찾기 및 인증 등에 사용)
     */
    @Column()
    private String phoneNumber;

    /**
     * 계정 활성화 여부
     */
    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * 계정 잠금 시각 (로그인 실패 등으로 잠긴 시간 저장)
     */
    @Column()
    private LocalDateTime lockedAt;

    /**
     * 비밀번호 초기화 시각 (비밀번호 변경 혹은 초기화된 시간)
     */
    @Column()
    private LocalDateTime passwordResetAt;

    /**
     * 최종 로그인 시각
     */
    @Column()
    private LocalDateTime lastLoginAt;
}
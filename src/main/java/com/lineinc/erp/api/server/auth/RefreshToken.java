package com.lineinc.erp.api.server.auth;

import com.lineinc.erp.api.server.common.BaseEntity;
import com.lineinc.erp.api.server.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

@Entity
public class RefreshToken extends BaseEntity {

    /**
     * 연관 유저 정보 (User - Company : 다대일 관계)
     */
    @OneToOne
    private User user;

    private LocalDateTime expiryDate;
}
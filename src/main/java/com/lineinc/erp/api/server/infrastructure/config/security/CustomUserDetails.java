package com.lineinc.erp.api.server.infrastructure.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final Long userId;
    private final String username;
    private final String password;
    @Getter
    private final boolean active;
    @Getter
    private final boolean deleted;

    public CustomUserDetails(Long userId, String username, String password, boolean active, boolean deleted) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.active = active;
        this.deleted = deleted;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
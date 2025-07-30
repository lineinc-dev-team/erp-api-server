package com.lineinc.erp.api.server.config.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails, java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final Long userId;
    private final String username;
    private final String password;
    @Getter
    private final boolean active;

    public CustomUserDetails(Long userId, String username, String password, boolean active) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.active = active;
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
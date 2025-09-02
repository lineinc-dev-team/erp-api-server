package com.lineinc.erp.api.server.domain.common.service;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new BadCredentialsException(ValidationMessages.USER_NOT_FOUND));
        return new CustomUserDetails(user.getId(), user.getLoginId(), user.getPasswordHash(), user.isActive(),
                user.isDeleted());
    }
}
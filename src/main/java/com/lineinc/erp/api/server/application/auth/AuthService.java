package com.lineinc.erp.api.server.application.auth;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadCredentialsException(ValidationMessages.USER_NOT_FOUND));
    }
}
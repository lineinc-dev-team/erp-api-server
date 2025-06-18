package com.lineinc.erp.api.server.application.auth;

import com.lineinc.erp.api.server.domain.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadCredentialsException("해당 사용자가 존재하지 않습니다."));
    }
}
package com.lineinc.erp.api.server.application.users;

import com.lineinc.erp.api.server.domain.users.Users;
import com.lineinc.erp.api.server.domain.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsersService {


    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.default-password}")
    private String defaultPassword;


    @Transactional(readOnly = true)
    public Users getUserByLoginIdOrThrow(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void resetPassword(String loginId) {
        Users user = getUserByLoginIdOrThrow(loginId);
        String encodedPassword = passwordEncoder.encode(defaultPassword);
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void updateLastLoginAt(Users user) {
        user.updateLastLoginAt(LocalDateTime.now());
        usersRepository.save(user);
    }
}

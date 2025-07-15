package com.lineinc.erp.api.server.application.user;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserInfoResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${user.default-password}")
    private String defaultPassword;


    @Transactional(readOnly = true)
    public User getUserByLoginIdOrThrow(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void resetPassword(String loginId) {
        User user = getUserByLoginIdOrThrow(loginId);
        String encodedPassword = passwordEncoder.encode(defaultPassword);
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void updateLastLoginAt(User user) {
        user.updateLastLoginAt(OffsetDateTime.now());
    }

    @Transactional(readOnly = true)
    public Page<UserInfoResponse> getAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable).map(UserInfoResponse::from);
    }

    @Transactional
    public void createUser(CreateUserRequest request) {
        if (usersRepository.existsByLoginId(request.loginId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.LOGIN_ID_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.username())
                .loginId(request.loginId())
                .passwordHash(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .isActive(request.isActive())
                .build();

        usersRepository.save(user);
    }

}

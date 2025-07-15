package com.lineinc.erp.api.server.application.user;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.MailUtils;
import com.lineinc.erp.api.server.common.util.PasswordUtils;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserInfoResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.CreateUserRequest;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UserListRequest;
import lombok.RequiredArgsConstructor;
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
    private final MailUtils mailUtils;

    @Transactional(readOnly = true)
    public User getUserByLoginIdOrThrow(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void resetPassword(long id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 임시 비밀번호 생성
        String tempPassword = PasswordUtils.generateDefaultPassword();

        // 이메일로 임시 비밀번호 발송
        mailUtils.sendPasswordResetEmail(user.getEmail(), user.getUsername(), tempPassword);

        // 암호화된 비밀번호로 업데이트
        String encodedPassword = passwordEncoder.encode(tempPassword);
        user.updatePassword(encodedPassword);
        usersRepository.save(user);
    }

    @Transactional
    public void updateLastLoginAt(User user) {
        user.updateLastLoginAt(OffsetDateTime.now());
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserInfoResponse> getAllUsers(UserListRequest request, Pageable pageable) {
        return usersRepository.findAll(request, pageable);
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
                .landlineNumber(request.landlineNumber())
                .email(request.email())
                .isActive(request.isActive())
                .build();

        usersRepository.save(user);
    }

}

package com.lineinc.erp.api.server.application.user;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.common.util.ExcelExportUtils;
import com.lineinc.erp.api.server.common.util.MailUtils;
import com.lineinc.erp.api.server.common.util.PasswordUtils;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserInfoResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.CreateUserRequest;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UserListRequest;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

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

    @Transactional(readOnly = true)
    public Workbook downloadExcel(UserListRequest request, Sort sort, List<String> fields) {
        List<UserInfoResponse> userInfoResponses = usersRepository.findAllWithoutPaging(request, sort);
        return ExcelExportUtils.generateWorkbook(
                userInfoResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue
        );
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "loginId" -> "사용자 ID";
            case "username" -> "사용자 이름";
            case "roleName" -> "권한그룹";
            case "isActive" -> "계정상태";
            case "lastLoginAt" -> "최종접속일";
            case "createdAt" -> "생성일자";
            case "updatedAt" -> "최종수정일";
            case "updateBy" -> "수정자";
            case "memo" -> "비고";
            default -> null;
        };
    }

    private String getExcelCellValue(UserInfoResponse user, String field) {
        return switch (field) {
            case "id" -> String.valueOf(user.id());
            case "loginId" -> user.loginId();
            case "username" -> user.username();
            case "roleName" -> user.roles().isEmpty() ? "" : user.roles().get(0).name();
            case "isActive" -> user.isActive() ? "Y" : "N";
            case "lastLoginAt" ->
                    user.lastLoginAt() != null ? DateTimeFormatUtils.DATE_FORMATTER_YMD.format(user.lastLoginAt()) : "";
            case "createdAt" -> DateTimeFormatUtils.DATE_FORMATTER_YMD.format(user.createdAt());
            case "updatedAt" -> DateTimeFormatUtils.DATE_FORMATTER_YMD.format(user.updatedAt());
            case "updateBy" -> user.updatedBy();
            case "memo" -> user.memo();
            default -> null;
        };
    }

}

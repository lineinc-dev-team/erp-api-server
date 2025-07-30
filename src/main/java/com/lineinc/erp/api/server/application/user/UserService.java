package com.lineinc.erp.api.server.application.user;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.common.util.ExcelExportUtils;
import com.lineinc.erp.api.server.presentation.v1.user.dto.response.UserDetailResponse;
import org.springframework.beans.factory.annotation.Value;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.CreateUserRequest;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.DeleteUsersRequest;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UpdateUserRequest;
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
import org.springframework.data.domain.Slice;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${USER_DEFAULT_PASSWORD}")
    private String defaultPassword;

    @Transactional(readOnly = true)
    public User getUserByLoginIdOrThrow(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void resetPassword(long id) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(defaultPassword);
        user.updatePassword(encodedPassword);
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(UserListRequest request, Pageable pageable) {
        return usersRepository.findAll(request, pageable);
    }

    @Transactional
    public void createUser(CreateUserRequest request) {
        if (usersRepository.existsByLoginId(request.loginId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.LOGIN_ID_ALREADY_EXISTS);
        }

        System.out.println("request = " + request);
        User user = User.builder()
                .username(request.username())
                .loginId(request.loginId())
                .department(request.departmentId() != null ? Department.builder().id(request.departmentId()).build() : null)
                .grade(request.gradeId() != null ? Grade.builder().id(request.gradeId()).build() : null)
                .position(request.positionId() != null ? Position.builder().id(request.positionId()).build() : null)
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
        List<UserResponse> userResponses = usersRepository.findAllWithoutPaging(request, sort);
        return ExcelExportUtils.generateWorkbook(
                userResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue
        );
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "loginId" -> "사용자 ID";
            case "username" -> "이름";
            case "department" -> "부서";
            case "grade" -> "직급";
            case "position" -> "직책";
            case "phoneNumber" -> "휴대폰";
            case "isActive" -> "계정상태";
            case "lastLoginAt" -> "최종접속일";
            case "createdAt" -> "생성일자";
            case "updatedAt" -> "최종수정일";
            case "updatedBy" -> "수정자";
            case "memo" -> "비고";
            default -> null;
        };
    }

    private String getExcelCellValue(UserResponse user, String field) {
        return switch (field) {
            case "id" -> String.valueOf(user.id());
            case "loginId" -> user.loginId();
            case "username" -> user.username();
            case "department" -> user.department();
            case "grade" -> user.grade();
            case "position" -> user.position();
            case "phoneNumber" -> user.phoneNumber() != null ? user.phoneNumber() : "";
            case "isActive" -> user.isActive() ? "Y" : "N";
            case "lastLoginAt" -> DateTimeFormatUtils.formatKoreaLocalDate(user.lastLoginAt());
            case "createdAt" -> DateTimeFormatUtils.formatKoreaLocalDate(user.createdAt());
            case "updatedAt" -> DateTimeFormatUtils.formatKoreaLocalDate(user.updatedAt());
            case "updatedBy" -> user.updatedBy();
            case "memo" -> user.memo();
            default -> null;
        };
    }

    @Transactional
    public void deleteUsersByIds(DeleteUsersRequest request) {
        List<User> users = usersRepository.findAllById(request.userIds());
        if (users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND);
        }

        for (User user : users) {
            user.markAsDeleted();
        }

        usersRepository.saveAll(users);
    }

    @Transactional
    public void updateUser(Long id, UpdateUserRequest request) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));

        user.updateFrom(request, passwordEncoder);
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Slice<UserResponse.UserSimpleResponse> searchUsersByName(String keyword, Pageable pageable) {
        Slice<User> userSlice;

        if (keyword == null || keyword.isBlank()) {
            userSlice = usersRepository.findAllBy(pageable);
        } else {
            userSlice = usersRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
        }

        return userSlice.map(UserResponse.UserSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public User getUserByIdOrThrow(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
    }

    @Transactional
    public void updateLastLoginAt(Long userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.updateLastLoginAt(); // 이건 쿼리 나감
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long id) {
        User user = usersRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        return UserDetailResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = usersRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public User getUserEntity(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
    }
}


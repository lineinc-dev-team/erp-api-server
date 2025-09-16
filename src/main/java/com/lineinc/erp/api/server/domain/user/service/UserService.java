package com.lineinc.erp.api.server.domain.user.service;

import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.FormatUtils;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import com.lineinc.erp.api.server.domain.user.enums.UserChangeHistoryType;
import org.javers.core.Javers;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import org.springframework.beans.factory.annotation.Value;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.domain.user.repository.UserChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.request.PasswordChangeRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.BulkDeleteUsersRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.CreateUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.SearchUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.UpdateUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.UserChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.UserInfoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;
import java.util.Optional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    // Repository 의존성
    private final UserRepository usersRepository;
    private final UserChangeHistoryRepository userChangeHistoryRepository;
    private final DepartmentRepository departmentRepository;
    private final GradeRepository gradeRepository;
    private final PositionRepository positionRepository;

    // 기타 의존성
    private final PasswordEncoder passwordEncoder;
    private final Javers javers;

    // 설정값
    @Value("${USER_DEFAULT_PASSWORD:line1234}")
    private String defaultPassword;

    @Transactional
    public void resetPassword(long id) {
        User user = getUserByIdOrThrow(id);
        String encodedPassword = passwordEncoder.encode(defaultPassword);
        user.resetPassword(encodedPassword);
        usersRepository.save(user);

        UserChangeHistory changeHistory = UserChangeHistory.builder()
                .user(user)
                .type(UserChangeHistoryType.BASIC)
                .description(ValidationMessages.PASSWORD_RESET)
                .build();
        userChangeHistoryRepository.save(changeHistory);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(SearchUserRequest request, Pageable pageable) {
        return usersRepository.findAll(request, pageable);
    }

    @Transactional
    public void createUser(CreateUserRequest request) {
        if (usersRepository.existsByLoginIdAndDeletedFalse(request.loginId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.LOGIN_ID_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.username())
                .loginId(request.loginId())
                .department(Department.builder().id(request.departmentId()).build())
                .grade(Grade.builder().id(request.gradeId()).build())
                .position(Position.builder().id(request.positionId()).build())
                .passwordHash(passwordEncoder.encode(defaultPassword))
                .phoneNumber(request.phoneNumber())
                .landlineNumber(request.landlineNumber())
                .email(request.email())
                .isActive(request.isActive())
                .isHeadOffice(request.isHeadOffice())
                .memo(request.memo())
                .build();

        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(SearchUserRequest request, Sort sort, List<String> fields) {
        List<UserResponse> userResponses = usersRepository.findAllWithoutPaging(request, sort);
        return ExcelExportUtils.generateWorkbook(
                userResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);
    }

    private String getExcelHeaderName(String field) {
        return switch (field) {
            case "id" -> "No.";
            case "loginId" -> "사용자 ID";
            case "username" -> "사용자 이름";
            case "department" -> "부서";
            case "grade" -> "직급";
            case "position" -> "직책";
            case "phoneNumber" -> "개인 휴대폰";
            case "landlineNumber" -> "전화번호";
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
            case "phoneNumber" -> user.phoneNumber();
            case "landlineNumber" -> user.landlineNumber();
            case "isActive" -> FormatUtils.toYesNo(user.isActive());
            case "lastLoginAt" -> DateTimeFormatUtils.formatKoreaLocalDate(user.lastLoginAt());
            case "createdAt" -> DateTimeFormatUtils.formatKoreaLocalDate(user.createdAt());
            case "updatedAt" -> DateTimeFormatUtils.formatKoreaLocalDate(user.updatedAt());
            case "updatedBy" -> user.updatedBy();
            case "memo" -> user.memo();
            default -> null;
        };
    }

    @Transactional
    public void deleteUsersByIds(BulkDeleteUsersRequest request) {
        List<User> users = usersRepository.findAllById(request.userIds());
        if (request.userIds().size() != users.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND);
        }
        users.forEach(User::markAsDeletedWithRoles);
        usersRepository.saveAll(users);
    }

    @Transactional
    public void updateUser(Long id, UpdateUserRequest request) {
        User user = getUserByIdOrThrow(id);
        user.syncTransientFields();
        User oldUserSnapshot = JaversUtils.createSnapshot(javers, user, User.class);

        Department department = departmentRepository.findById(request.departmentId()).orElse(null);
        Grade grade = gradeRepository.findById(request.gradeId()).orElse(null);
        Position position = positionRepository.findById(request.positionId()).orElse(null);

        // 사용자 정보 업데이트
        user.updateFrom(request, department, grade, position);
        usersRepository.save(user);

        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers,
                javers.compare(oldUserSnapshot, user));

        if (!simpleChanges.isEmpty()) {
            UserChangeHistory changeHistory = UserChangeHistory.builder()
                    .user(user)
                    .type(UserChangeHistoryType.BASIC)
                    .changes(javers.getJsonConverter().toJson(simpleChanges))
                    .build();
            userChangeHistoryRepository.save(changeHistory);
        }

        // 변경 이력 메모 업데이트
        Optional.ofNullable(request.changeHistories())
                .ifPresent(histories -> histories
                        .forEach(historyRequest -> userChangeHistoryRepository.findById(historyRequest.id())
                                .ifPresent(hist -> hist.setMemo(historyRequest.memo()))));
    }

    @Transactional(readOnly = true)
    public Slice<UserResponse.UserSimpleResponse> searchUsersByName(String keyword, Boolean hasRole,
            Pageable pageable) {
        Slice<User> userSlice = usersRepository.findAllByKeywordAndExcludeUsername(
                keyword, AppConstants.ADMIN_USERNAME, hasRole, pageable);
        return userSlice.map(UserResponse.UserSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public User getUserByIdOrThrow(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
    }

    @Transactional
    public void updateLastLoginAt(Long userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setLastLoginAt(OffsetDateTime.now());
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserDetail(Long id) {
        User user = usersRepository.findByIdWithRoles(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        return UserInfoResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        User user = usersRepository.findByIdWithRoles(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public User getUserEntity(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = getUserByIdOrThrow(userId);
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        user.setRequirePasswordReset(false);
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Slice<UserChangeHistoryResponse> getUserChangeHistoriesSlice(Long userId, Pageable pageable) {
        User user = getUserByIdOrThrow(userId);
        Slice<UserChangeHistory> historySlice = userChangeHistoryRepository.findByUser(user, pageable);
        return historySlice.map(UserChangeHistoryResponse::from);
    }

    /**
     * 유저 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<UserChangeHistoryResponse> getUserChangeHistoriesWithPaging(Long userId, Pageable pageable) {
        User user = getUserByIdOrThrow(userId);
        Page<UserChangeHistory> historyPage = userChangeHistoryRepository.findByUserWithPaging(user,
                pageable);
        return historyPage.map(UserChangeHistoryResponse::from);
    }

    public List<Long> getAccessibleSiteIds(User user) {
        if (!user.hasRoles()) {
            return List.of();
        }

        boolean hasGlobalAccess = user.hasGlobalSiteProcessAccess();
        if (hasGlobalAccess) {
            return null;
        }

        return user.getUserRoles().stream()
                .flatMap(role -> role.getRole().getSiteProcesses().stream())
                .filter(siteProcess -> siteProcess.getSite() != null && !siteProcess.getSite().isDeleted())
                .map(siteProcess -> siteProcess.getSite().getId())
                .distinct()
                .toList();
    }
}

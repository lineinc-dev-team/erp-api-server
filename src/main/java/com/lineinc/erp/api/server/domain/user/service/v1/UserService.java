package com.lineinc.erp.api.server.domain.user.service.v1;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Workbook;
import org.javers.core.Javers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lineinc.erp.api.server.domain.common.service.S3FileService;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.enums.ExcelDownloadHistoryType;
import com.lineinc.erp.api.server.domain.exceldownloadhistory.service.ExcelDownloadHistoryService;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import com.lineinc.erp.api.server.domain.user.enums.UserChangeHistoryType;
import com.lineinc.erp.api.server.domain.user.repository.UserChangeHistoryRepository;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.infrastructure.config.security.CustomUserDetails;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.request.PasswordChangeRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.BulkDeleteUsersRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.CreateUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.SearchUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.UpdateUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.UserChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.UserInfoResponse;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.shared.util.FormatUtils;
import com.lineinc.erp.api.server.shared.util.JaversUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    // 기타 의존성
    private final PasswordEncoder passwordEncoder;
    private final Javers javers;

    private final S3FileService s3FileService;
    private final ExcelDownloadHistoryService excelDownloadHistoryService;
    // 설정값
    @Value("${USER_DEFAULT_PASSWORD:line1234}")
    private String defaultPassword;

    @Transactional
    public void resetPassword(final long id) {
        final User user = getUserByIdOrThrow(id);
        final String encodedPassword = passwordEncoder.encode(defaultPassword);
        user.resetPassword(encodedPassword);
        usersRepository.save(user);

        final UserChangeHistory changeHistory = UserChangeHistory.builder()
                .user(user)
                .type(UserChangeHistoryType.BASIC)
                .description(ValidationMessages.PASSWORD_RESET)
                .build();
        userChangeHistoryRepository.save(changeHistory);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(final SearchUserRequest request, final Pageable pageable) {
        return usersRepository.findAll(request, pageable);
    }

    @Transactional
    public void createUser(final CreateUserRequest request, final Long userId) {
        if (usersRepository.existsByLoginIdAndDeletedFalse(request.loginId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationMessages.LOGIN_ID_ALREADY_EXISTS);
        }

        final User user = User.builder()
                .username(request.username())
                .loginId(request.loginId())
                .department(Department.builder().id(request.departmentId()).build())
                .grade(Grade.builder().id(request.gradeId()).build())
                .passwordHash(passwordEncoder.encode(defaultPassword))
                .phoneNumber(request.phoneNumber())
                .landlineNumber(request.landlineNumber())
                .email(request.email())
                .isActive(request.isActive())
                .isHeadOffice(request.isHeadOffice())
                .memo(request.memo())
                .build();

        usersRepository.save(user);

        final UserChangeHistory changeHistory = UserChangeHistory.builder()
                .user(user)
                .description(ValidationMessages.INITIAL_CREATION)
                .updatedByUser(getUserByIdOrThrow(userId))
                .build();
        userChangeHistoryRepository.save(changeHistory);
    }

    @Transactional(readOnly = true)
    public Workbook downloadExcel(final CustomUserDetails user, final SearchUserRequest request, final Sort sort,
            final List<String> fields) {
        final List<UserResponse> userResponses = usersRepository.findAllWithoutPaging(request, sort);
        final Workbook workbook = ExcelExportUtils.generateWorkbook(
                userResponses,
                fields,
                this::getExcelHeaderName,
                this::getExcelCellValue);

        final String fileUrl = s3FileService.uploadExcelToS3(workbook,
                ExcelDownloadHistoryType.ACCOUNT.name());

        excelDownloadHistoryService.recordDownload(
                ExcelDownloadHistoryType.ACCOUNT,
                getUserByIdOrThrow(user.getUserId()), fileUrl);

        return workbook;
    }

    private String getExcelHeaderName(final String field) {
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

    private String getExcelCellValue(final UserResponse user, final String field) {
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
    public void deleteUsersByIds(final BulkDeleteUsersRequest request) {
        final List<User> users = usersRepository.findAllById(request.userIds());
        if (request.userIds().size() != users.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND);
        }
        users.forEach(User::markAsDeletedWithRoles);
        usersRepository.saveAll(users);
    }

    @Transactional
    public void updateUser(final Long id, final UpdateUserRequest request, final Long userId) {
        final User user = getUserByIdOrThrow(id);
        user.syncTransientFields();
        final User oldUserSnapshot = JaversUtils.createSnapshot(javers, user, User.class);

        final Department department = departmentRepository.findById(request.departmentId()).orElse(null);
        final Grade grade = gradeRepository.findById(request.gradeId()).orElse(null);

        // 사용자 정보 업데이트
        user.updateFrom(request, department, grade);
        usersRepository.save(user);

        final List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers,
                javers.compare(oldUserSnapshot, user));

        if (!simpleChanges.isEmpty()) {
            final UserChangeHistory changeHistory = UserChangeHistory.builder()
                    .user(user)
                    .type(UserChangeHistoryType.BASIC)
                    .changes(javers.getJsonConverter().toJson(simpleChanges))
                    .updatedByUser(getUserByIdOrThrow(userId))
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
    public Slice<UserResponse.UserSimpleResponse> searchUsers(final String keyword, final String loginIdKeyword,
            final Boolean hasRole,
            final Pageable pageable) {
        final Slice<User> userSlice = usersRepository.findAllByKeywordAndExcludeUsername(
                keyword, loginIdKeyword, AppConstants.ADMIN_USERNAME, hasRole, pageable);
        return userSlice.map(UserResponse.UserSimpleResponse::from);
    }

    @Transactional(readOnly = true)
    public User getUserByIdOrThrow(final Long id) {
        return usersRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
    }

    @Transactional
    public void updateLastLoginAt(final Long userId) {
        final User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setLastLoginAt(OffsetDateTime.now());
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserDetail(final Long id) {
        final User user = usersRepository.findByIdWithRoles(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        return UserInfoResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(final Long id) {
        final User user = usersRepository.findByIdWithRoles(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public User getUserEntity(final Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
    }

    @Transactional
    public void changePassword(final Long userId, final PasswordChangeRequest request) {
        final User user = getUserByIdOrThrow(userId);
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        user.setRequirePasswordReset(false);
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Slice<UserChangeHistoryResponse> getUserChangeHistoriesSlice(final Long userId, final Pageable pageable,
            final Long loggedInUserId) {
        final User user = getUserByIdOrThrow(userId);
        final Slice<UserChangeHistory> historySlice = userChangeHistoryRepository.findByUser(user, pageable);
        return historySlice.map(history -> UserChangeHistoryResponse.from(history, loggedInUserId));
    }

    /**
     * 유저 변경 이력을 전체 개수와 함께 조회
     * 페이지 네비게이션이 필요한 경우 사용
     */
    @Transactional(readOnly = true)
    public Page<UserChangeHistoryResponse> getUserChangeHistoriesWithPaging(final Long userId,
            final Pageable pageable, final Long loggedInUserId) {
        final User user = getUserByIdOrThrow(userId);
        final Page<UserChangeHistory> historyPage = userChangeHistoryRepository.findByUserWithPaging(user,
                pageable);
        return historyPage.map(history -> UserChangeHistoryResponse.from(history, loggedInUserId));
    }

    public List<Long> getAccessibleSiteIds(final User user) {
        if (!user.hasRoles()) {
            return List.of();
        }

        final boolean hasGlobalAccess = user.hasGlobalSiteProcessAccess();
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

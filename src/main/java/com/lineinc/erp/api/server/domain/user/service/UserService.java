package com.lineinc.erp.api.server.domain.user.service;

import com.lineinc.erp.api.server.shared.message.ValidationMessages;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.ExcelExportUtils;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import com.lineinc.erp.api.server.domain.user.enums.UserChangeType;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import com.lineinc.erp.api.server.shared.util.JaversUtils;
import org.springframework.beans.factory.annotation.Value;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.domain.user.repository.UserChangeHistoryRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.request.PasswordChangeRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.user.CreateUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.user.BulkDeleteUsersRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.user.UpdateUserRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.user.UserChangeHistoryResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.response.user.UserInfoResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.user.SearchUserRequest;
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

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final GradeRepository gradeRepository;
    private final PositionRepository positionRepository;
    private final Javers javers;
    private final UserChangeHistoryRepository userChangeHistoryRepository;

    @Value("${USER_DEFAULT_PASSWORD:line1234}")
    private String defaultPassword;

    @Transactional(readOnly = true)
    public User getUserByLoginIdOrThrow(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void resetPassword(long id) {
        User user = usersRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(defaultPassword);
        user.updatePassword(encodedPassword);
        user.setRequirePasswordReset(true);
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(SearchUserRequest request, Pageable pageable) {
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
                .department(
                        request.departmentId() != null ? Department.builder().id(request.departmentId()).build() : null)
                .grade(request.gradeId() != null ? Grade.builder().id(request.gradeId()).build() : null)
                .position(request.positionId() != null ? Position.builder().id(request.positionId()).build() : null)
                .passwordHash(passwordEncoder.encode(defaultPassword))
                .phoneNumber(request.phoneNumber())
                .landlineNumber(request.landlineNumber())
                .email(request.email())
                .isActive(request.isActive())
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
            case "username" -> "이름";
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
            case "phoneNumber" -> user.phoneNumber() != null ? user.phoneNumber() : "";
            case "landlineNumber" -> user.landlineNumber() != null ? user.landlineNumber() : "";
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
    public void deleteUsersByIds(BulkDeleteUsersRequest request) {
        List<User> users = usersRepository.findAllById(request.userIds());
        if (request.userIds().size() != users.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND);
        }
        users.forEach(user -> {
            user.markAsDeleted();
            user.getUserRoles().forEach(UserRole::markAsDeleted);
        });
        usersRepository.saveAll(users);
    }

    @Transactional
    public void updateUser(Long id, UpdateUserRequest request) {
        User oldUser = usersRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));

        oldUser.syncTransientFields();
        User oldUserSnapshot = JaversUtils.createSnapshot(javers, oldUser, User.class);

        // 새로운 메서드들을 사용하여 사용자 정보 업데이트
        oldUser.updateFrom(request.username(), request.email(), request.phoneNumber(), request.landlineNumber(),
                request.memo(),
                request.departmentId() != null ? Department.builder().id(request.departmentId()).build() : null,
                request.gradeId() != null ? Grade.builder().id(request.gradeId()).build() : null,
                request.positionId() != null ? Position.builder().id(request.positionId()).build() : null,
                request.isActive());

        // 비밀번호 업데이트
        if (request.password() != null && !request.password().isBlank()) {
            oldUser.updatePassword(passwordEncoder.encode(request.password()));
        }

        usersRepository.save(oldUser);

        Diff diff = javers.compare(oldUserSnapshot, oldUser);

        List<Map<String, String>> simpleChanges = JaversUtils.extractModifiedChanges(javers, diff);
        String changesJson = javers.getJsonConverter().toJson(simpleChanges);
        if (!simpleChanges.isEmpty()) {
            UserChangeHistory changeHistory = UserChangeHistory.builder()
                    .user(oldUser)
                    .type(UserChangeType.BASIC)
                    .changes(changesJson)
                    .build();
            userChangeHistoryRepository.save(changeHistory);
        }

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (UpdateUserRequest.ChangeHistoryRequest changeHistoryRequest : request.changeHistories()) {
                userChangeHistoryRepository.findById(changeHistoryRequest.id())
                        .filter(hist -> hist.getUser().getId().equals(oldUser.getId()))
                        .ifPresent(hist -> {
                            hist.setMemo(changeHistoryRequest.memo());
                        });
            }
        }
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
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
    }

    @Transactional
    public void updateLastLoginAt(Long userId) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.updateLastLoginAt(); // 이건 쿼리 나감
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
        User user = usersRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        user.setRequirePasswordReset(false);
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Slice<UserChangeHistoryResponse> getUserChangeHistoriesSlice(Long userId, Pageable pageable) {
        // 유저 존재 확인 (필요 시)
        User user = usersRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));

        // UserChangeHistory를 user 기준으로 슬라이스 조회
        Slice<UserChangeHistory> historySlice = userChangeHistoryRepository.findByUser(user, pageable);

        // 엔티티를 DTO로 변환하여 슬라이스 반환
        return historySlice.map(UserChangeHistoryResponse::from);
    }

    public List<Long> getAccessibleSiteIds(User user) {
        // 유저 권한 없음 → 접근 불가 (빈 리스트 반환)
        if (user.getUserRoles() == null || user.getUserRoles().isEmpty()) {
            return List.of(); // 빈 리스트로 명시적 반환
        }

        // 글로벌 권한 보유 → 전체 접근 가능 → null 반환
        boolean hasGlobalAccess = user.getUserRoles().stream()
                .anyMatch(role -> role.getRole().isHasGlobalSiteProcessAccess());

        if (hasGlobalAccess) {
            return null; // 특별히 null 처리로 전체 조회를 허용
        }

        // 제한된 접근 권한 → siteId 목록 추출
        return user.getUserRoles().stream()
                .flatMap(role -> role.getRole().getSiteProcesses().stream())
                .map(siteProcess -> siteProcess.getSite().getId())
                .distinct()
                .toList();
    }
}

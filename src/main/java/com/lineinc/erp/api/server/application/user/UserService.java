package com.lineinc.erp.api.server.application.user;

import com.lineinc.erp.api.server.common.constant.ValidationMessages;
import com.lineinc.erp.api.server.common.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.common.util.ExcelExportUtils;
import com.lineinc.erp.api.server.domain.organization.repository.DepartmentRepository;
import com.lineinc.erp.api.server.domain.organization.repository.GradeRepository;
import com.lineinc.erp.api.server.domain.organization.repository.PositionRepository;
import com.lineinc.erp.api.server.domain.user.entity.UserChangeHistory;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.request.PasswordChangeRequest;
import com.lineinc.erp.api.server.presentation.v1.user.dto.response.UserChangeHistoryResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.response.UserDetailResponse;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.beans.factory.annotation.Value;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.organization.entity.Department;
import com.lineinc.erp.api.server.domain.organization.entity.Grade;
import com.lineinc.erp.api.server.domain.organization.entity.Position;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.domain.user.repository.UserChangeHistoryRepository;
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

import java.util.List;

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

    @Value("${USER_DEFAULT_PASSWORD}")
    private String defaultPassword;

    @Transactional(readOnly = true)
    public User getUserByLoginIdOrThrow(String loginId) {
        return usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void resetPassword(long id) {
        if (id == 1L) return; // 1번 계정은 비밀번호 초기화 금지
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(defaultPassword);
        user.updatePassword(encodedPassword);
        user.setRequirePasswordReset(true);
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

        User user = User.builder()
                .username(request.username())
                .loginId(request.loginId())
                .department(request.departmentId() != null ? Department.builder().id(request.departmentId()).build() : null)
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
        if (request.userIds().size() != users.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND);
        }
        users.forEach(User::markAsDeleted);
        usersRepository.saveAll(users);
    }

    @Transactional
    public void updateUser(Long id, UpdateUserRequest request) {
        User user = usersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        User.UserUpdateResult result = user.updateFrom(request, passwordEncoder, departmentRepository, gradeRepository, positionRepository);
        Diff diff = javers.compare(result.before(), result.after());

        if (request.changeHistories() != null && !request.changeHistories().isEmpty()) {
            for (UpdateUserRequest.ChangeHistoryRequest historyRequest : request.changeHistories()) {
                userChangeHistoryRepository.findById(historyRequest.id())
                        .filter(history -> history.getUser().getId().equals(user.getId()))
                        .ifPresent(history -> {
                            history.setMemo(historyRequest.memo());
                        });
            }
        }

        // 변경된 필드를 추적하여 사용자 친화적 메시지 생성
        String changeDetail = buildUserChangeDetail(diff);
        // 변경 사항이 있으면 이력 저장
        if (!changeDetail.isBlank()) {
            UserChangeHistory changeHistory = UserChangeHistory.builder()
                    .user(user)
                    .changeDetail(changeDetail)
                    .build();
            userChangeHistoryRepository.save(changeHistory);
        }

        usersRepository.save(user);
    }

    private String buildUserChangeDetail(Diff diff) {
        StringBuilder changeDetailBuilder = new StringBuilder();
        diff.getChanges().forEach(change -> {
            if (change instanceof ValueChange valueChange) {
                String propertyName = valueChange.getPropertyName();
                String label = switch (propertyName) {
                    case "username" -> "이름";
                    case "phoneNumber" -> "휴대폰";
                    case "landlineNumber" -> "연락처";
                    case "email" -> "이메일";
                    case "isActive" -> "계정상태";
                    case "memo" -> "비고";
                    case "departmentName" -> "부서";
                    case "gradeName" -> "직급";
                    case "positionName" -> "직책";
                    default -> null;
                };
                if (label != null) {
                    Object left = valueChange.getLeft();
                    Object right = valueChange.getRight();
                    String leftStr = left == null ? "" : left.toString();
                    String rightStr = right == null ? "" : right.toString();
                    if ("isActive".equals(propertyName)) {
                        leftStr = "true".equals(leftStr) ? "활성" : "비활성";
                        rightStr = "true".equals(rightStr) ? "활성" : "비활성";
                    }
                    if (rightStr.isBlank()) {
                        return;
                    }
                    changeDetailBuilder.append(label)
                            .append(" : ")
                            .append(leftStr)
                            .append(" → ")
                            .append(rightStr)
                            .append("\n");
                }
            }
        });
        return changeDetailBuilder.toString();
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

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        user.setRequirePasswordReset(false);
        usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Slice<UserChangeHistoryResponse> getUserChangeHistoriesSlice(Long userId, Pageable pageable) {
        // 유저 존재 확인 (필요 시)
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ValidationMessages.USER_NOT_FOUND));

        // UserChangeHistory를 user 기준으로 슬라이스 조회
        Slice<UserChangeHistory> historySlice = userChangeHistoryRepository.findByUser(user, pageable);

        // 엔티티를 DTO로 변환하여 슬라이스 반환
        return historySlice.map(UserChangeHistoryResponse::from);
    }
}

package com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response;

import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "로그인된 사용자 정보 응답")
public record UserResponse(
        @Schema(description = "사용자 ID", example = "123") Long id,

        @Schema(description = "로그인 ID", example = "admin01") String loginId,

        @Schema(description = "사용자 이름", example = "홍길동") String username,

        @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,

        @Schema(description = "이메일 주소", example = "user@example.com") String email,

        @Schema(description = "계정 상태", example = "true") Boolean isActive,

        @Schema(description = "생성 일시", example = "2025-07-15T10:00:00+09:00") OffsetDateTime createdAt,

        @Schema(description = "수정 일시", example = "2025-07-15T12:00:00+09:00") OffsetDateTime updatedAt,

        @Schema(description = "최종 로그인 일시", example = "2025-07-15T12:30:00+09:00") OffsetDateTime lastLoginAt,

        @Schema(description = "전화번호", example = "02-123-4567") String landlineNumber,

        @Schema(description = "수정자", example = "관리자") String updatedBy,

        @Schema(description = "비고", example = "특이사항 없음") String memo,

        @Schema(description = "부서 이름", example = "개발팀") String department,

        @Schema(description = "직급 이름", example = "대리") String grade,

        @Schema(description = "직책 이름", example = "팀장") String position,

        @Schema(description = "최초 로그인 시 비밀번호 재설정 여부", example = "true") Boolean requirePasswordReset,

        @Schema(description = "사용자 권한 목록") List<RoleSummaryResponse> roles) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getUsername(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt(),
                user.getLandlineNumber(),
                user.getUpdatedBy(),
                user.getMemo(),
                user.getDepartment() != null ? user.getDepartment().getName() : null,
                user.getGrade() != null ? user.getGrade().getName() : null,
                user.getPosition() != null ? user.getPosition().getName() : null,
                user.isRequirePasswordReset(),
                user.getUserRoles().stream()
                        .max((ur1, ur2) -> ur1.getCreatedAt().compareTo(ur2.getCreatedAt())) // 가장 최근 것
                        .map(UserRole::getRole)
                        .map(role -> new RoleSummaryResponse(role.getId(), role.getName(), role.isDeleted()))
                        .map(List::of) // 단일 요소를 리스트로 변환
                        .orElse(List.of())); // 없으면 빈 리스트
    }

    @Schema(description = "권한 그룹 요약 응답")
    public static record RoleSummaryResponse(
            @Schema(description = "권한 그룹 ID", example = "1") Long id,

            @Schema(description = "권한 그룹 이름", example = "어드민") String name,

            @Schema(description = "삭제 여부", example = "false") Boolean deleted) {
    }

    @Schema(description = "간단한 유저 응답")
    public static record UserSimpleResponse(
            @Schema(description = "사용자 ID", example = "123") Long id,

            @Schema(description = "로그인 ID", example = "admin01") String loginId,

            @Schema(description = "사용자 이름", example = "홍길동") String username,

            @Schema(description = "사용자 부서", example = "개발팀") String department) {
        public static UserSimpleResponse from(User user) {
            return new UserSimpleResponse(user.getId(), user.getLoginId(), user.getUsername(),
                    user.getDepartment() != null ? user.getDepartment().getName() : null);
        }
    }
}
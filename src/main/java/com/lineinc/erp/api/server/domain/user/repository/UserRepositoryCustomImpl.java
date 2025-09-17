package com.lineinc.erp.api.server.domain.user.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.user.entity.QUser;
import com.lineinc.erp.api.server.domain.user.entity.QUserRole;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.SearchUserRequest;
import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 Repository 커스텀 구현체
 * QueryDSL을 사용한 복잡한 동적 쿼리 처리
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    // 정렬 가능한 필드 매핑
    private final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", user.id,
            "loginId", user.loginId,
            "username", user.username,
            "createdAt", user.createdAt,
            "updatedAt", user.updatedAt,
            "lastLoginAt", user.lastLoginAt);

    @Override
    public Page<UserResponse> findAll(final SearchUserRequest request, final Pageable pageable) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        // 데이터 조회
        final List<User> content = queryFactory
                .selectFrom(user)
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        final Long totalCount = queryFactory
                .select(user.count())
                .from(user)
                .where(condition)
                .fetchOne();
        final long total = Objects.requireNonNullElse(totalCount, 0L);

        // DTO 변환
        final List<UserResponse> responses = content.stream()
                .map(UserResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public List<UserResponse> findAllWithoutPaging(final SearchUserRequest request, final Sort sort) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        final List<User> users = queryFactory
                .selectFrom(user)
                .where(condition)
                .orderBy(orders)
                .fetch();

        return users.stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * 검색 조건을 QueryDSL BooleanBuilder로 변환합니다.
     * 
     * @param request 검색 요청
     * @return QueryDSL 조건 빌더
     */
    private BooleanBuilder buildCondition(final SearchUserRequest request) {
        final BooleanBuilder builder = new BooleanBuilder();

        // 기본 조건: 관리자 제외, 삭제되지 않은 사용자
        builder.and(user.loginId.ne(AppConstants.ADMIN_LOGIN_ID));
        builder.and(user.deleted.eq(false));

        // 사용자명 검색
        if (StringUtils.hasText(request.username())) {
            builder.and(user.username.containsIgnoreCase(request.username().trim()));
        }

        // 활성 상태 필터
        if (request.isActive() != null) {
            builder.and(user.isActive.eq(request.isActive()));
        }

        // 역할 필터
        if (request.roleId() != null) {
            final QUserRole userRole = QUserRole.userRole;
            builder.and(user.id.in(
                    queryFactory.select(userRole.user.id)
                            .from(userRole)
                            .where(userRole.role.id.eq(request.roleId()))));
        }

        // 생성일 범위 필터
        if (request.createdStartDate() != null) {
            final OffsetDateTime startTime = DateTimeFormatUtils.toUtcStartOfDay(request.createdStartDate());
            builder.and(user.createdAt.goe(startTime));
        }

        if (request.createdEndDate() != null) {
            final OffsetDateTime endTime = DateTimeFormatUtils.toUtcEndOfDay(request.createdEndDate());
            builder.and(user.createdAt.lt(endTime));
        }

        // 마지막 로그인일 범위 필터
        if (request.lastLoginStartDate() != null) {
            final OffsetDateTime startTime = DateTimeFormatUtils.toUtcStartOfDay(request.lastLoginStartDate());
            builder.and(user.lastLoginAt.goe(startTime));
        }

        if (request.lastLoginEndDate() != null) {
            final OffsetDateTime endTime = DateTimeFormatUtils.toUtcEndOfDay(request.lastLoginEndDate());
            builder.and(user.lastLoginAt.lt(endTime));
        }

        // 부서 필터
        if (request.departmentId() != null) {
            builder.and(user.department.id.eq(request.departmentId()));
        }

        // 직급 필터
        if (request.gradeId() != null) {
            builder.and(user.grade.id.eq(request.gradeId()));
        }

        // 직책 필터
        if (request.positionId() != null) {
            builder.and(user.position.id.eq(request.positionId()));
        }

        return builder;
    }
}

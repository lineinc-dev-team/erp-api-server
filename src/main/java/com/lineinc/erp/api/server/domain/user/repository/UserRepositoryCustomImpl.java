package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.lineinc.erp.api.server.domain.user.entity.QUser;
import com.lineinc.erp.api.server.domain.user.entity.QUserRole;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.interfaces.rest.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.user.dto.request.user.SearchUserRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import static com.lineinc.erp.api.server.shared.constant.AppConstants.KOREA_ZONE_OFFSET;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    private final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", user.id,
            "loginId", user.loginId,
            "username", user.username,
            "createdAt", user.createdAt,
            "updatedAt", user.updatedAt,
            "lastLoginAt", user.lastLoginAt
    );

    @Override
    public Page<UserResponse> findAll(SearchUserRequest request, Pageable pageable) {

        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(
                pageable,
                SORT_FIELDS
        );

        List<User> content = queryFactory
                .selectFrom(user)
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(user.count())
                .from(user)
                .where(condition)
                .fetchOne();
        long total = Objects.requireNonNullElse(totalCount, 0L);

        List<UserResponse> responses = content.stream()
                .map(UserResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    private BooleanBuilder buildCondition(SearchUserRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.id.ne(1L));

        if (StringUtils.hasText(request.username())) {
            builder.and(user.username.containsIgnoreCase(request.username().trim()));
        }

        if (request.isActive() != null) {
            builder.and(user.isActive.eq(request.isActive()));
        }
        QUserRole userRole = QUserRole.userRole;
        if (request.roleId() != null) {
            builder.and(user.id.in(
                    queryFactory.select(userRole.user.id)
                            .from(userRole)
                            .where(userRole.role.id.eq(request.roleId()))
            ));
        }

        if (request.createdStartDate() != null) {
            builder.and(user.createdAt.goe(request.createdStartDate().atStartOfDay().atOffset(KOREA_ZONE_OFFSET)));
        }

        if (request.createdEndDate() != null) {
            builder.and(user.createdAt.lt(request.createdEndDate().plusDays(1).atStartOfDay().atOffset(KOREA_ZONE_OFFSET)));
        }

        if (request.lastLoginStartDate() != null) {
            builder.and(user.lastLoginAt.goe(request.lastLoginStartDate().atStartOfDay().atOffset(KOREA_ZONE_OFFSET)));
        }

        if (request.lastLoginEndDate() != null) {
            builder.and(user.lastLoginAt.lt(request.lastLoginEndDate().plusDays(1).atStartOfDay().atOffset(KOREA_ZONE_OFFSET)));
        }

        if (request.departmentId() != null) {
            builder.and(user.department.id.eq(request.departmentId()));
        }
        if (request.gradeId() != null) {
            builder.and(user.grade.id.eq(request.gradeId()));
        }
        if (request.positionId() != null) {
            builder.and(user.position.id.eq(request.positionId()));
        }

        if (request.hasRole() != null) {
            if (request.hasRole()) {
                // 어떤 역할이라도 하나 이상 가지고 있는 유저만 조회
                builder.and(user.id.in(
                        queryFactory.select(userRole.user.id)
                                .from(userRole)
                                .distinct()
                ));
            } else {
                // 어떤 역할도 하나도 가지고 있지 않은 유저만 조회
                builder.and(user.id.notIn(
                        queryFactory.select(userRole.user.id)
                                .from(userRole)
                                .distinct()
                ));
            }
        }

        return builder;
    }

    @Override
    public List<UserResponse> findAllWithoutPaging(SearchUserRequest request, Sort sort) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        List<User> users = queryFactory
                .selectFrom(user)
                .where(condition)
                .orderBy(orders)
                .fetch();

        return users.stream()
                .map(UserResponse::from)
                .toList();
    }
}


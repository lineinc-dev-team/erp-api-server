package com.lineinc.erp.api.server.domain.user.repository;

import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.domain.user.entity.QUser;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.presentation.v1.auth.dto.response.UserResponse;
import com.lineinc.erp.api.server.presentation.v1.user.dto.request.UserListRequest;
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

import java.time.ZoneOffset;

import static com.lineinc.erp.api.server.common.constant.AppConstants.KOREA_ZONE_OFFSET;

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
    public Page<UserResponse> findAll(UserListRequest request, Pageable pageable) {

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

    private BooleanBuilder buildCondition(UserListRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.username())) {
            builder.and(user.username.containsIgnoreCase(request.username().trim()));
        }

        if (request.isActive() != null) {
            builder.and(user.isActive.eq(request.isActive()));
        }
        if (request.roleId() != null) {
            builder.and(user.roles.any().id.eq(request.roleId()));
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

        return builder;
    }

    @Override
    public List<UserResponse> findAllWithoutPaging(UserListRequest request, Sort sort) {
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


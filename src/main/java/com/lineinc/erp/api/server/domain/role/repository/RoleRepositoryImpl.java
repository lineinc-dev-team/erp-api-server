package com.lineinc.erp.api.server.domain.role.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.role.entity.QRole;
import com.lineinc.erp.api.server.domain.role.entity.QRoleSiteProcess;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.domain.user.entity.QUser;
import com.lineinc.erp.api.server.domain.user.entity.QUserRole;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.RoleUserListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.UserWithRolesListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.RoleUserListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.RolesResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 상수 정의
    private static final Long ADMIN_ROLE_ID = 1L;
    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QRole.role.id,
            "name", QRole.role.name,
            "createdAt", QRole.role.createdAt,
            "updatedAt", QRole.role.updatedAt);

    @Override
    public Page<RolesResponse> findAll(final UserWithRolesListRequest request, final Pageable pageable) {
        final String search = extractSearchTerm(request);
        final boolean hasUserSearch = isValidSearchTerm(search);

        final QRole role = QRole.role;
        final QUser user = QUser.user;

        final BooleanExpression whereCondition = buildWhereCondition(role, user, search);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        // 단일 쿼리로 데이터와 카운트 조회
        final List<Role> content = fetchRolesWithDetails(role, user, whereCondition, orders, pageable, hasUserSearch);
        final long total = fetchTotalCount(role, user, whereCondition, hasUserSearch);

        return new PageImpl<>(
                content.stream().map(RolesResponse::from).toList(),
                pageable,
                total);
    }

    @Override
    public Page<RoleUserListResponse> findUsersByRoleId(final Long roleId, final RoleUserListRequest request,
            final Pageable pageable) {
        final QUser user = QUser.user;
        final QRole role = QRole.role;
        final QUserRole userRole = QUserRole.userRole;

        final BooleanExpression searchPredicate = buildSearchPredicate(user, request.search());

        final List<RoleUserListResponse> content = fetchUsersByRole(user, role, userRole, roleId, searchPredicate,
                pageable);
        final long total = fetchUserCountByRole(user, role, userRole, roleId, searchPredicate);

        return new PageImpl<>(content, pageable, total);
    }

    // ===== 헬퍼 메서드들 =====

    private String extractSearchTerm(final UserWithRolesListRequest request) {
        return request != null ? request.userSearch() : null;
    }

    private boolean isValidSearchTerm(final String search) {
        return search != null && !search.trim().isEmpty();
    }

    private BooleanExpression buildWhereCondition(final QRole role, final QUser user, final String search) {
        final BooleanExpression baseCondition = role.id.ne(ADMIN_ROLE_ID).and(role.deleted.eq(false));

        // 사용자 검색 조건이 있을 때만 사용자 관련 조건 추가
        if (search != null && !search.trim().isEmpty()) {
            final BooleanExpression searchCondition = buildSearchPredicate(user, search);
            return baseCondition.and(searchCondition);
        }

        return baseCondition;
    }

    private BooleanExpression buildSearchPredicate(final QUser user, final String search) {
        // 삭제되지 않은 사용자만 조회
        final BooleanExpression baseCondition = user.deleted.eq(false);

        if (search == null || search.trim().isEmpty()) {
            return baseCondition;
        }

        final String trimmedSearch = search.trim();
        final BooleanExpression searchCondition = user.username.containsIgnoreCase(trimmedSearch)
                .or(user.loginId.containsIgnoreCase(trimmedSearch));

        return baseCondition.and(searchCondition);
    }

    private List<Role> fetchRolesWithDetails(final QRole role, final QUser user, final BooleanExpression whereCondition,
            final OrderSpecifier<?>[] orders, final Pageable pageable, final boolean hasUserSearch) {
        final QRoleSiteProcess roleSiteProcess = QRoleSiteProcess.roleSiteProcess;
        final QSite site = QSite.site;
        final QSiteProcess process = QSiteProcess.siteProcess;

        if (hasUserSearch) {
            return queryFactory
                    .selectFrom(role)
                    .leftJoin(role.userRoles, QUserRole.userRole).fetchJoin()
                    .leftJoin(QUserRole.userRole.user, user).fetchJoin()
                    .leftJoin(role.siteProcesses, roleSiteProcess).fetchJoin()
                    .leftJoin(roleSiteProcess.site, site).fetchJoin()
                    .leftJoin(roleSiteProcess.process, process).fetchJoin()
                    .where(whereCondition)
                    .orderBy(orders)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else {
            return queryFactory
                    .selectFrom(role)
                    .leftJoin(role.userRoles, QUserRole.userRole).fetchJoin()
                    .leftJoin(QUserRole.userRole.user, user).fetchJoin()
                    .leftJoin(role.siteProcesses, roleSiteProcess).fetchJoin()
                    .leftJoin(roleSiteProcess.site, site).fetchJoin()
                    .leftJoin(roleSiteProcess.process, process).fetchJoin()
                    .where(whereCondition)
                    .orderBy(orders)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }
    }

    private long fetchTotalCount(final QRole role, final QUser user, final BooleanExpression whereCondition,
            final boolean hasUserSearch) {
        if (hasUserSearch) {
            return queryFactory
                    .select(role.countDistinct())
                    .from(role)
                    .leftJoin(role.userRoles, QUserRole.userRole)
                    .leftJoin(QUserRole.userRole.user, user)
                    .where(whereCondition)
                    .fetchOne();
        } else {
            return queryFactory
                    .select(role.count())
                    .from(role)
                    .where(whereCondition)
                    .fetchOne();
        }
    }

    private List<RoleUserListResponse> fetchUsersByRole(final QUser user, final QRole role, final QUserRole userRole,
            final Long roleId, final BooleanExpression searchPredicate,
            final Pageable pageable) {
        return queryFactory
                .selectDistinct(user)
                .from(user)
                .join(user.userRoles, userRole)
                .join(userRole.role, role)
                .where(role.id.eq(roleId).and(searchPredicate))
                .orderBy(user.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(userObj -> RoleUserListResponse.from(userObj, roleId))
                .toList();
    }

    private long fetchUserCountByRole(final QUser user, final QRole role, final QUserRole userRole,
            final Long roleId, final BooleanExpression searchPredicate) {
        final Long totalCount = queryFactory
                .select(user.countDistinct())
                .from(user)
                .join(user.userRoles, userRole)
                .join(userRole.role, role)
                .where(role.id.eq(roleId).and(searchPredicate))
                .fetchOne();

        return Objects.requireNonNullElse(totalCount, 0L);
    }
}
package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import com.lineinc.erp.api.server.domain.role.entity.QRole;
import com.lineinc.erp.api.server.domain.user.entity.QUser;
import com.lineinc.erp.api.server.domain.user.entity.QUserRole;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.RoleUserListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.request.UserWithRolesListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.RoleUserListResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response.RolesResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

import java.util.Map;

import com.lineinc.erp.api.server.domain.role.entity.QRoleSiteProcess;
import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;

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
            "updatedAt", QRole.role.updatedAt,
            "siteName", QSite.site.name);

    @Override
    public Page<RolesResponse> findAll(UserWithRolesListRequest request, Pageable pageable) {
        QRole role = QRole.role;
        QUser user = QUser.user;
        QRoleSiteProcess roleSiteProcess = QRoleSiteProcess.roleSiteProcess;
        QSite site = QSite.site;
        QSiteProcess process = QSiteProcess.siteProcess;

        String search = extractSearchTerm(request);
        BooleanExpression whereCondition = buildWhereCondition(role, user, search);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        // 1단계: 페이징된 ID 목록 조회
        List<Long> roleIds = fetchRoleIds(role, user, whereCondition, orders, pageable);

        // 2단계: ID로 상세 정보 조회
        List<Role> content = fetchRoleDetails(role, user, roleSiteProcess, site, process, roleIds, orders);

        // 3단계: 총 개수 조회
        long total = fetchTotalCount(role, user, whereCondition);

        List<RolesResponse> responses = content.stream()
                .map(RolesResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public Page<RoleUserListResponse> findUsersByRoleId(Long roleId, RoleUserListRequest request, Pageable pageable) {
        QUser user = QUser.user;
        QRole role = QRole.role;
        QUserRole userRole = QUserRole.userRole;

        BooleanExpression searchPredicate = buildSearchPredicate(user, request.search());

        List<RoleUserListResponse> content = fetchUsersByRole(user, role, userRole, roleId, searchPredicate, pageable);
        long total = fetchUserCountByRole(user, role, userRole, roleId, searchPredicate);

        return new PageImpl<>(content, pageable, total);
    }

    // 헬퍼 메서드들
    private String extractSearchTerm(UserWithRolesListRequest request) {
        return request != null ? request.userSearch() : null;
    }

    private BooleanExpression buildWhereCondition(QRole role, QUser user, String search) {
        return role.id.ne(ADMIN_ROLE_ID).and(buildSearchPredicate(user, search));
    }

    private BooleanExpression buildSearchPredicate(QUser user, String search) {
        if (search == null || search.trim().isEmpty()) {
            return null;
        }

        String trimmedSearch = search.trim();
        return user.username.containsIgnoreCase(trimmedSearch)
                .or(user.loginId.containsIgnoreCase(trimmedSearch));
    }

    private List<Long> fetchRoleIds(QRole role, QUser user, BooleanExpression whereCondition,
            OrderSpecifier<?>[] orders, Pageable pageable) {
        return queryFactory
                .select(role.id)
                .from(role)
                .leftJoin(role.userRoles, QUserRole.userRole)
                .leftJoin(QUserRole.userRole.user, user)
                .where(whereCondition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private List<Role> fetchRoleDetails(QRole role, QUser user, QRoleSiteProcess roleSiteProcess,
            QSite site, QSiteProcess process, List<Long> roleIds,
            OrderSpecifier<?>[] orders) {
        return queryFactory
                .selectFrom(role)
                .leftJoin(role.userRoles, QUserRole.userRole).fetchJoin()
                .leftJoin(QUserRole.userRole.user, user).fetchJoin()
                .leftJoin(role.siteProcesses, roleSiteProcess).fetchJoin()
                .leftJoin(roleSiteProcess.site, site).fetchJoin()
                .leftJoin(roleSiteProcess.process, process).fetchJoin()
                .where(role.id.in(roleIds))
                .orderBy(orders)
                .fetch();
    }

    private long fetchTotalCount(QRole role, QUser user, BooleanExpression whereCondition) {
        Long totalCount = queryFactory
                .select(role.countDistinct())
                .from(role)
                .leftJoin(role.userRoles, QUserRole.userRole)
                .leftJoin(QUserRole.userRole.user, user)
                .leftJoin(role.siteProcesses, QRoleSiteProcess.roleSiteProcess)
                .leftJoin(QRoleSiteProcess.roleSiteProcess.site, QSite.site)
                .where(whereCondition)
                .fetchOne();

        return Objects.requireNonNullElse(totalCount, 0L);
    }

    private List<RoleUserListResponse> fetchUsersByRole(QUser user, QRole role, QUserRole userRole,
            Long roleId, BooleanExpression searchPredicate,
            Pageable pageable) {
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

    private long fetchUserCountByRole(QUser user, QRole role, QUserRole userRole,
            Long roleId, BooleanExpression searchPredicate) {
        Long totalCount = queryFactory
                .select(user.countDistinct())
                .from(user)
                .join(user.userRoles, userRole)
                .join(userRole.role, role)
                .where(role.id.eq(roleId).and(searchPredicate))
                .fetchOne();

        return Objects.requireNonNullElse(totalCount, 0L);
    }
}
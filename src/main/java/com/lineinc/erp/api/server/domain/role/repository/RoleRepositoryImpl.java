package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.RoleUserListRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.request.UserWithRolesListRequest;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RoleUserListResponse;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RolesResponse;
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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.lineinc.erp.api.server.common.util.PageableUtils;
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

    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QRole.role.id,
            "name", QRole.role.name,
            "createdAt", QRole.role.createdAt,
            "updatedAt", QRole.role.updatedAt
    );

    @Override
    public Page<RolesResponse> findAll(UserWithRolesListRequest request, Pageable pageable) {
        QRole role = QRole.role;
        QUser user = QUser.user;
        QRoleSiteProcess roleSiteProcess = QRoleSiteProcess.roleSiteProcess;
        QSite site = QSite.site;
        QSiteProcess process = QSiteProcess.siteProcess;

        String search = (request != null) ? request.userSearch() : null;

        BooleanExpression whereCondition = role.id.ne(1L).and(containsSearch(user, search));

        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        // fetchJoin 사용: role.userRoles 컬렉션, siteProcesses, site, process를 한 번에 같이 조회
        List<Role> content = queryFactory
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

        Long totalCount = queryFactory
                .select(role.countDistinct())
                .from(role)
                .leftJoin(role.userRoles, QUserRole.userRole)
                .join(QUserRole.userRole.user, user)
                .where(whereCondition)
                .fetchOne();

        long total = Objects.requireNonNullElse(totalCount, 0L);

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

        BooleanExpression searchPredicate = containsSearch(user, request.search());

        List<RoleUserListResponse> content = queryFactory
                .selectDistinct(user)
                .from(user)
                .join(user.userRoles, userRole)
                .join(userRole.role, role)
                .where(role.id.eq(roleId)
                        .and(searchPredicate))
                .orderBy(user.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(RoleUserListResponse::from)
                .toList();

        Long totalCount = queryFactory
                .select(user.countDistinct())
                .from(user)
                .join(user.userRoles, userRole)
                .join(userRole.role, role)
                .where(role.id.eq(roleId)
                        .and(searchPredicate))
                .fetchOne();

        long total = Objects.requireNonNullElse(totalCount, 0L);
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression containsSearch(QUser user, String search) {
        if (search == null) {
            return null;
        }
        String trimmed = search.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return user.username.containsIgnoreCase(trimmed)
                .or(user.loginId.containsIgnoreCase(trimmed));
    }

}
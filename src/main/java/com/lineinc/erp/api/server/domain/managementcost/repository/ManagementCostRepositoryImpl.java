package com.lineinc.erp.api.server.domain.managementcost.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.QManagementCost;
import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request.ManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.response.ManagementCostResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * ManagementCostRepositoryCustom의 구현체.
 * ManagementCost 목록 조회 시, QueryDSL을 사용하여 조건 검색 및 페이징 처리.
 */
@Repository
@RequiredArgsConstructor
public class ManagementCostRepositoryImpl implements ManagementCostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QManagementCost managementCost = QManagementCost.managementCost;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;

    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QManagementCost.managementCost.id,
            "paymentDate", QManagementCost.managementCost.paymentDate,
            "createdAt", QManagementCost.managementCost.createdAt,
            "updatedAt", QManagementCost.managementCost.updatedAt);

    @Override
    public Page<ManagementCostResponse> findAll(final ManagementCostListRequest request, final Pageable pageable,
            final List<Long> accessibleSiteIds) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);
        if (accessibleSiteIds != null) {
            if (accessibleSiteIds.isEmpty()) {
                return Page.empty(pageable);
            }
            condition.and(site.id.in(accessibleSiteIds));
        }
        final List<ManagementCost> content = queryFactory
                .selectFrom(managementCost)
                .distinct()
                .leftJoin(managementCost.site, site).fetchJoin()
                .leftJoin(managementCost.siteProcess, siteProcess).fetchJoin()
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Long totalCount = queryFactory
                .select(managementCost.count())
                .from(managementCost)
                .leftJoin(managementCost.site, site)
                .leftJoin(managementCost.siteProcess, siteProcess)
                .where(condition)
                .fetchOne();

        final long total = Objects.requireNonNullElse(totalCount, 0L);

        final List<ManagementCostResponse> responses = content.stream()
                .map(ManagementCostResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    private BooleanBuilder buildCondition(final ManagementCostListRequest request) {
        final BooleanBuilder builder = new BooleanBuilder();

        builder.and(managementCost.deleted.eq(false));

        if (StringUtils.hasText(request.siteName())) {
            builder.and(site.name.containsIgnoreCase(request.siteName().trim()));
        }
        if (StringUtils.hasText(request.processName())) {
            builder.and(siteProcess.name.containsIgnoreCase(request.processName().trim()));
        }
        if (StringUtils.hasText(request.outsourcingCompanyName())) {
            builder.and(
                    managementCost.outsourcingCompany.name.containsIgnoreCase(request.outsourcingCompanyName().trim()));
        }
        if (request.itemType() != null) {
            builder.and(managementCost.itemType.eq(request.itemType()));
        }
        if (StringUtils.hasText(request.itemTypeDescription())) {
            builder.and(managementCost.itemTypeDescription.containsIgnoreCase(request.itemTypeDescription().trim()));
        }
        if (request.paymentStartDate() != null) {
            builder.and(managementCost.paymentDate.goe(
                    DateTimeFormatUtils.getUtcDateRange(request.paymentStartDate())[0]));
        }
        if (request.paymentEndDate() != null) {
            builder.and(managementCost.paymentDate.lt(
                    DateTimeFormatUtils.getUtcDateRange(request.paymentEndDate())[1]));
        }

        return builder;
    }

    @Override
    public List<ManagementCost> findAllWithoutPaging(final ManagementCostListRequest request, final Sort sort,
            final List<Long> accessibleSiteIds) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);
        if (accessibleSiteIds != null) {
            if (accessibleSiteIds.isEmpty()) {
                return List.of();
            }
            condition.and(site.id.in(accessibleSiteIds));
        }
        return queryFactory
                .selectFrom(managementCost)
                .distinct()
                .leftJoin(managementCost.site, site).fetchJoin()
                .leftJoin(managementCost.siteProcess, siteProcess).fetchJoin()
                .where(condition)
                .orderBy(orders)
                .fetch();
    }
}

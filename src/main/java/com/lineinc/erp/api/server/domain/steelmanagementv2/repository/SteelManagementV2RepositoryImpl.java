package com.lineinc.erp.api.server.domain.steelmanagementv2.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.QSteelManagementDetailV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.QSteelManagementV2;
import com.lineinc.erp.api.server.domain.steelmanagementv2.entity.SteelManagementV2;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.request.SteelManagementV2ListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v2.steelmanagement.dto.response.SteelManagementV2Response;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * SteelManagementV2RepositoryCustom의 구현체.
 * QueryDSL을 사용하여 조건 검색 및 페이징 처리.
 */
@Repository
@RequiredArgsConstructor
public class SteelManagementV2RepositoryImpl implements SteelManagementV2RepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QSteelManagementV2 steelManagementV2 = QSteelManagementV2.steelManagementV2;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;
    private final QSteelManagementDetailV2 detail = QSteelManagementDetailV2.steelManagementDetailV2;

    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QSteelManagementV2.steelManagementV2.id,
            "createdAt", QSteelManagementV2.steelManagementV2.createdAt,
            "updatedAt", QSteelManagementV2.steelManagementV2.updatedAt);

    @Override
    public Page<SteelManagementV2Response> findAll(
            final SteelManagementV2ListRequest request,
            final Pageable pageable,
            final List<Long> accessibleSiteIds) {

        final BooleanBuilder builder = buildCondition(request);
        if (accessibleSiteIds != null) {
            if (accessibleSiteIds.isEmpty()) {
                return Page.empty(pageable);
            }
            builder.and(steelManagementV2.site.id.in(accessibleSiteIds));
        }
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        final List<SteelManagementV2> content = queryFactory
                .selectFrom(steelManagementV2)
                .distinct()
                .leftJoin(steelManagementV2.site, site).fetchJoin()
                .leftJoin(steelManagementV2.siteProcess, siteProcess).fetchJoin()
                .leftJoin(steelManagementV2.details, detail)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders)
                .fetch();

        final Long totalCount = queryFactory
                .select(steelManagementV2.count())
                .from(steelManagementV2)
                .where(builder)
                .fetchOne();
        final long total = java.util.Objects.requireNonNullElse(totalCount, 0L);

        final List<SteelManagementV2Response> responses = content.stream()
                .map(SteelManagementV2Response::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public List<SteelManagementV2Response> findAllWithoutPaging(
            final SteelManagementV2ListRequest request,
            final org.springframework.data.domain.Sort sort,
            final List<Long> accessibleSiteIds) {

        final BooleanBuilder builder = buildCondition(request);
        if (accessibleSiteIds != null) {
            if (accessibleSiteIds.isEmpty()) {
                return List.of();
            }
            builder.and(steelManagementV2.site.id.in(accessibleSiteIds));
        }
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        final List<SteelManagementV2> content = queryFactory
                .selectFrom(steelManagementV2)
                .distinct()
                .leftJoin(steelManagementV2.site, site).fetchJoin()
                .leftJoin(steelManagementV2.siteProcess, siteProcess).fetchJoin()
                .leftJoin(steelManagementV2.details, detail)
                .where(builder)
                .orderBy(orders)
                .fetch();

        return content.stream()
                .map(SteelManagementV2Response::from)
                .toList();
    }

    private BooleanBuilder buildCondition(final SteelManagementV2ListRequest request) {
        final BooleanBuilder builder = new BooleanBuilder();
        builder.and(steelManagementV2.deleted.isFalse());

        // 현장명 검색
        if (StringUtils.hasText(request.siteName())) {
            builder.and(site.name.containsIgnoreCase(request.siteName().trim()));
        }

        // 공정명 검색
        if (StringUtils.hasText(request.siteProcessName())) {
            builder.and(siteProcess.name.containsIgnoreCase(request.siteProcessName().trim()));
        }

        // 등록일 검색
        if (request.createdStartDate() != null) {
            final OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.createdStartDate());
            builder.and(steelManagementV2.createdAt.goe(dateRange[0]));
        }

        // 등록일 종료일 검색
        if (request.createdEndDate() != null) {
            final OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.createdEndDate());
            builder.and(steelManagementV2.createdAt.lt(dateRange[1]));
        }
        return builder;
    }

    @Override
    public List<SteelManagementV2> findBySiteAndSiteProcessAndYearMonthLessThanEqual(
            final Site site,
            final SiteProcess siteProcess,
            final String yearMonth) {
        final BooleanBuilder builder = new BooleanBuilder();
        builder.and(steelManagementV2.deleted.isFalse());
        builder.and(steelManagementV2.site.eq(site));
        builder.and(steelManagementV2.siteProcess.eq(siteProcess));

        // 날짜 필터링은 서비스 레이어에서 수행 (detail 단위로 정확하게 필터링하기 위함)
        // Repository에서는 현장과 공정에 해당하는 모든 SteelManagementV2를 조회
        return queryFactory
                .selectFrom(steelManagementV2)
                .leftJoin(steelManagementV2.site, this.site).fetchJoin()
                .leftJoin(steelManagementV2.siteProcess, this.siteProcess).fetchJoin()
                .where(builder)
                .orderBy(steelManagementV2.createdAt.asc())
                .fetch();
    }
}

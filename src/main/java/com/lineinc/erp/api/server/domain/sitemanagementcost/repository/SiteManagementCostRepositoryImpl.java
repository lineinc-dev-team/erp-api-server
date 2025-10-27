package com.lineinc.erp.api.server.domain.sitemanagementcost.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.QSiteManagementCost;
import com.lineinc.erp.api.server.domain.sitemanagementcost.entity.SiteManagementCost;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.request.SiteManagementCostListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.sitemanagementcost.dto.response.SiteManagementCostResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * 현장관리비 커스텀 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class SiteManagementCostRepositoryImpl implements SiteManagementCostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QSiteManagementCost siteManagementCost = QSiteManagementCost.siteManagementCost;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;

    // 정렬 가능한 필드 매핑
    private final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", siteManagementCost.id,
            "yearMonth", siteManagementCost.yearMonth,
            "createdAt", siteManagementCost.createdAt,
            "updatedAt", siteManagementCost.updatedAt);

    @Override
    public Page<SiteManagementCostResponse> findAll(
            final SiteManagementCostListRequest request,
            final Pageable pageable) {

        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        // 데이터 조회
        final List<SiteManagementCost> content = queryFactory
                .selectFrom(siteManagementCost)
                .leftJoin(siteManagementCost.site, site).fetchJoin()
                .leftJoin(siteManagementCost.siteProcess, siteProcess).fetchJoin()
                .where(
                        yearMonthBetween(request.startYearMonth(), request.endYearMonth()),
                        siteNameContains(request.siteName()),
                        siteProcessNameContains(request.siteProcessName()),
                        siteManagementCost.deleted.eq(false))
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        final Long totalCount = queryFactory
                .select(siteManagementCost.count())
                .from(siteManagementCost)
                .leftJoin(siteManagementCost.site, site)
                .leftJoin(siteManagementCost.siteProcess, siteProcess)
                .where(
                        yearMonthBetween(request.startYearMonth(), request.endYearMonth()),
                        siteNameContains(request.siteName()),
                        siteProcessNameContains(request.siteProcessName()),
                        siteManagementCost.deleted.eq(false))
                .fetchOne();
        final long total = Objects.requireNonNullElse(totalCount, 0L);

        // DTO 변환
        final List<SiteManagementCostResponse> responses = content.stream()
                .map(SiteManagementCostResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    // 동적 쿼리 조건 메서드들
    private BooleanExpression yearMonthBetween(final String startYearMonth, final String endYearMonth) {
        if (startYearMonth != null && endYearMonth != null) {
            return siteManagementCost.yearMonth.between(startYearMonth, endYearMonth);
        } else if (startYearMonth != null) {
            return siteManagementCost.yearMonth.goe(startYearMonth);
        } else if (endYearMonth != null) {
            return siteManagementCost.yearMonth.loe(endYearMonth);
        }
        return null;
    }

    private BooleanExpression siteNameContains(final String siteName) {
        return siteName != null ? site.name.containsIgnoreCase(siteName) : null;
    }

    private BooleanExpression siteProcessNameContains(final String siteProcessName) {
        return siteProcessName != null ? siteProcess.name.containsIgnoreCase(siteProcessName) : null;
    }
}

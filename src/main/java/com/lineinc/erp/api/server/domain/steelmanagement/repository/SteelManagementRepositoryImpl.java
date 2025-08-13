package com.lineinc.erp.api.server.domain.steelmanagement.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.QSteelManagement;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.QSteelManagementDetail;
import com.lineinc.erp.api.server.domain.steelmanagement.entity.SteelManagement;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.request.SteelManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.steelmanagement.dto.response.SteelManagementResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * SteelManagementRepositoryCustom의 구현체.
 * SteelManagement 목록 조회 시, QueryDSL을 사용하여 조건 검색 및 페이징 처리.
 */
@Repository
@RequiredArgsConstructor
public class SteelManagementRepositoryImpl implements SteelManagementRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QSteelManagement steelManagement = QSteelManagement.steelManagement;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;
    private final QSteelManagementDetail detail = QSteelManagementDetail.steelManagementDetail;

    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QSteelManagement.steelManagement.id,
            "paymentDate", QSteelManagement.steelManagement.paymentDate,
            "createdAt", QSteelManagement.steelManagement.createdAt,
            "updatedAt", QSteelManagement.steelManagement.updatedAt);

    @Override
    public Page<SteelManagementResponse> findAll(SteelManagementListRequest request, Pageable pageable) {

        BooleanBuilder builder = buildCondition(request);

        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        List<SteelManagement> content = queryFactory
                .selectFrom(steelManagement)
                .distinct()
                .leftJoin(steelManagement.site, site).fetchJoin()
                .leftJoin(steelManagement.siteProcess, siteProcess).fetchJoin()
                .leftJoin(steelManagement.details, detail)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders)
                .fetch();

        Long totalCount = queryFactory
                .select(steelManagement.count())
                .from(steelManagement)
                .where(builder)
                .fetchOne();
        long total = java.util.Objects.requireNonNullElse(totalCount, 0L);

        List<SteelManagementResponse> responses = content.stream()
                .map(SteelManagementResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public List<SteelManagement> findAllWithoutPaging(SteelManagementListRequest request, Sort sort) {

        BooleanBuilder builder = buildCondition(request);

        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        return queryFactory
                .selectFrom(steelManagement)
                .distinct()
                .leftJoin(steelManagement.site, site).fetchJoin()
                .leftJoin(steelManagement.siteProcess, siteProcess).fetchJoin()
                .leftJoin(steelManagement.details, detail)
                .where(builder)
                .orderBy(orders)
                .fetch();
    }

    private BooleanBuilder buildCondition(SteelManagementListRequest request) {

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.siteName())) {
            builder.and(site.name.containsIgnoreCase(request.siteName().trim()));
        }
        if (StringUtils.hasText(request.processName())) {
            builder.and(siteProcess.name.containsIgnoreCase(request.processName().trim()));
        }
        if (request.paymentStartDate() != null) {
            builder.and(steelManagement.paymentDate.goe(
                    DateTimeFormatUtils.getUtcDateRange(request.paymentStartDate())[0]));
        }
        if (request.paymentEndDate() != null) {
            builder.and(steelManagement.paymentDate.lt(
                    DateTimeFormatUtils.getUtcDateRange(request.paymentEndDate())[1]));
        }
        if (request.type() != null) {
            builder.and(steelManagement.type.eq(request.type()));
        }
        if (StringUtils.hasText(request.itemName())) {
            builder.and(
                    JPAExpressions.selectOne()
                            .from(detail)
                            .where(detail.steelManagement.eq(steelManagement)
                                    .and(detail.name.containsIgnoreCase(request.itemName().trim())))
                            .exists());
        }

        return builder;
    }
}

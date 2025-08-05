package com.lineinc.erp.api.server.domain.site.repository;

import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.domain.site.entity.*;
import com.lineinc.erp.api.server.domain.site.enums.SiteProcessStatus;
import com.lineinc.erp.api.server.domain.site.enums.SiteType;
import com.lineinc.erp.api.server.presentation.v1.site.dto.request.SiteListRequest;
import com.lineinc.erp.api.server.presentation.v1.site.dto.response.SiteResponse;
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

import static com.lineinc.erp.api.server.common.constant.AppConstants.KOREA_ZONE_OFFSET;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SiteRepositoryCustom의 구현체.
 * Site 목록 조회 시, QueryDSL을 사용하여 조건 검색 및 페이징 처리.
 */
@Repository
@RequiredArgsConstructor
public class SiteRepositoryImpl implements SiteRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;

    // 정렬 필드를 미리 정의하여 정적 매핑.
    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QSite.site.id,
            "name", QSite.site.name,
            "startedAt", QSite.site.startedAt,
            "createdAt", QSite.site.createdAt,
            "updatedAt", QSite.site.updatedAt
    );

    /**
     * Site 목록을 요청 조건(request)과 Pageable 정보에 따라 조회.
     *
     * @param request           검색 조건
     * @param pageable          페이징 및 정렬 정보
     * @param accessibleSiteIds 접근 가능한 사이트 ID 리스트
     * @return SiteResponse 리스트를 담은 Page 객체
     */
    @Override
    public Page<SiteResponse> findAll(SiteListRequest request, Pageable pageable, List<Long> accessibleSiteIds) {
        BooleanBuilder condition = buildCondition(request);
        if (accessibleSiteIds != null) {
            if (accessibleSiteIds.isEmpty()) {
                return Page.empty(pageable);
            }
            condition.and(site.id.in(accessibleSiteIds));
        }
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(
                pageable,
                SORT_FIELDS
        );

        List<Site> content = queryFactory
                .selectFrom(site)
                .leftJoin(site.clientCompany).fetchJoin()
                .leftJoin(site.processes, siteProcess)
                .fetchJoin()
                .distinct()
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리를 별도로 수행 (성능 최적화를 위해 fetchResults 대신 직접 분리)
        Long totalCount = queryFactory
                .select(site.count())
                .from(site)
                .where(condition)
                .fetchOne();
        long total = Objects.requireNonNullElse(totalCount, 0L);

        List<SiteResponse> responses = content.stream()
                .map(SiteResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public List<Site> findAllWithoutPaging(SiteListRequest request, Sort sort, List<Long> accessibleSiteIds) {
        BooleanBuilder condition = buildCondition(request);
        if (accessibleSiteIds != null) {
            if (accessibleSiteIds.isEmpty()) {
                return List.of();
            }
            condition.and(site.id.in(accessibleSiteIds));
        }
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        return queryFactory
                .selectFrom(site)
                .leftJoin(site.clientCompany).fetchJoin()
                .leftJoin(site.processes, siteProcess).fetchJoin()
                .distinct()
                .where(condition)
                .orderBy(orders)
                .fetch();
    }

    /**
     * 검색 조건을 생성하는 메서드.
     *
     * @param request 검색 요청 객체
     * @return BooleanBuilder (QueryDSL 조건 객체)
     */
    private BooleanBuilder buildCondition(SiteListRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.name())) {
            builder.and(site.name.containsIgnoreCase(request.name().trim()));
        }

        SiteType type = request.type();
        if (type != null) {
            builder.and(site.type.eq(type));
        }
        if (StringUtils.hasText(request.processName())) {
            builder.and(site.processes.any().name.containsIgnoreCase(request.processName().trim()));
        }
        if (StringUtils.hasText(request.managerName())) {
            builder.and(site.processes.any().manager.username.containsIgnoreCase(request.managerName().trim()));
        }
        if (StringUtils.hasText(request.city())) {
            builder.and(site.city.eq(request.city().trim()));
        }
        if (StringUtils.hasText(request.district())) {
            builder.and(site.district.eq(request.district().trim()));
        }

        List<SiteProcessStatus> siteProcessStatuses = request.processStatuses();
        if (siteProcessStatuses != null && !siteProcessStatuses.isEmpty()) {
            builder.and(site.processes.any().status.in(siteProcessStatuses));
        }
        if (StringUtils.hasText(request.clientCompanyName())) {
            builder.and(site.clientCompany.name.containsIgnoreCase(request.clientCompanyName().trim()));
        }
        if (StringUtils.hasText(request.createdBy())) {
            builder.and(site.createdBy.containsIgnoreCase(request.createdBy().trim()));
        }

        if (request.startDate() != null) {
            builder.and(site.startedAt.goe(
                    request.startDate()
                            .atStartOfDay()
                            .atOffset(KOREA_ZONE_OFFSET)
            ));
        }

        if (request.endDate() != null) {
            builder.and(site.endedAt.lt(
                    request.endDate()
                            .plusDays(1)
                            .atStartOfDay()
                            .atOffset(KOREA_ZONE_OFFSET)
            ));
        }

        if (request.createdStartDate() != null) {
            builder.and(site.createdAt.goe(
                    request.createdStartDate()
                            .atStartOfDay()
                            .atOffset(KOREA_ZONE_OFFSET)
            ));
        }

        if (request.createdEndDate() != null) {
            builder.and(site.createdAt.lt(
                    request.createdEndDate()
                            .plusDays(1)
                            .atStartOfDay()
                            .atOffset(KOREA_ZONE_OFFSET)
            ));
        }

        return builder;
    }

}
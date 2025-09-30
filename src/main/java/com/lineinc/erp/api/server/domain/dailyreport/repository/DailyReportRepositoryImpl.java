package com.lineinc.erp.api.server.domain.dailyreport.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.dailyreport.entity.QDailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request.DailyReportListSearchRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.response.DailyReportListResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DailyReportRepositoryImpl implements DailyReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QDailyReport dailyReport = QDailyReport.dailyReport;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;

    // 정렬 가능한 필드 매핑
    private final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", dailyReport.id,
            "reportDate", dailyReport.reportDate,
            "createdAt", dailyReport.createdAt,
            "updatedAt", dailyReport.updatedAt,
            "completedAt", dailyReport.completedAt);

    @Override
    public Page<DailyReportListResponse> findAllBySearchConditions(
            final DailyReportListSearchRequest request,
            final Pageable pageable) {

        // 동적 검색 조건 생성
        final BooleanExpression condition = buildCondition(request);

        // 정렬 조건 생성
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        // 데이터 조회
        final List<DailyReportListResponse> content = queryFactory
                .select(dailyReport)
                .from(dailyReport)
                .leftJoin(dailyReport.site, site).fetchJoin()
                .leftJoin(dailyReport.siteProcess, siteProcess).fetchJoin()
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(DailyReportListResponse::from)
                .toList();

        // 전체 개수 조회
        final Long total = queryFactory
                .select(dailyReport.count())
                .from(dailyReport)
                .leftJoin(dailyReport.site, site)
                .leftJoin(dailyReport.siteProcess, siteProcess)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 검색 조건 빌드
     */
    private BooleanExpression buildCondition(final DailyReportListSearchRequest request) {
        BooleanExpression condition = dailyReport.deleted.eq(false);

        // 현장명 검색
        if (StringUtils.hasText(request.siteName())) {
            condition = condition.and(site.name.containsIgnoreCase(request.siteName().trim()));
        }

        // 공정명 검색
        if (StringUtils.hasText(request.processName())) {
            condition = condition.and(siteProcess.name.containsIgnoreCase(request.processName().trim()));
        }

        // 시작일 필터
        if (request.startDate() != null) {
            final OffsetDateTime startDateTime = DateTimeFormatUtils.toUtcStartOfDay(request.startDate());
            condition = condition.and(dailyReport.reportDate.goe(startDateTime));
        }

        // 종료일 필터
        if (request.endDate() != null) {
            final OffsetDateTime endDateTime = DateTimeFormatUtils.toUtcEndOfDay(request.endDate());
            condition = condition.and(dailyReport.reportDate.loe(endDateTime));
        }

        // 마감 여부 필터
        if (request.isCompleted() != null) {
            if (request.isCompleted()) {
                condition = condition.and(
                        dailyReport.status.eq(DailyReportStatus.COMPLETED)
                                .or(dailyReport.status.eq(DailyReportStatus.AUTO_COMPLETED)));
            } else {
                condition = condition.and(dailyReport.status.eq(DailyReportStatus.PENDING));
            }
        }

        // 증빙 제출 여부 필터
        // true: 증빙이 하나라도 누락된 항목 조회 (증빙 미제출 목록)
        // false: 증빙이 모두 제출된 항목 조회 (증빙 완료 목록)
        if (request.isEvidenceSubmitted() != null) {
            if (request.isEvidenceSubmitted()) {
                // 하나라도 false면 조회됨
                condition = condition.and(
                        dailyReport.employeeEvidenceSubmitted.eq(false)
                                .or(dailyReport.directContractEvidenceSubmitted.eq(false))
                                .or(dailyReport.outsourcingEvidenceSubmitted.eq(false))
                                .or(dailyReport.equipmentEvidenceSubmitted.eq(false))
                                .or(dailyReport.fuelEvidenceSubmitted.eq(false))
                                .or(dailyReport.sitePhotoSubmitted.eq(false)));
            } else {
                // 모두 true여야 조회됨
                condition = condition.and(
                        dailyReport.employeeEvidenceSubmitted.eq(true)
                                .and(dailyReport.directContractEvidenceSubmitted.eq(true))
                                .and(dailyReport.outsourcingEvidenceSubmitted.eq(true))
                                .and(dailyReport.equipmentEvidenceSubmitted.eq(true))
                                .and(dailyReport.fuelEvidenceSubmitted.eq(true))
                                .and(dailyReport.sitePhotoSubmitted.eq(true)));
            }
        }

        return condition;
    }
}

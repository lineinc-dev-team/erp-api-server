package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagementDetail;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.QMaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.QMaterialManagementDetail;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany;
import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request.MaterialManagementListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.response.MaterialManagementResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MaterialManagementRepositoryImpl implements MaterialManagementRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QMaterialManagement materialManagement = QMaterialManagement.materialManagement;
    private final QMaterialManagementDetail materialManagementDetail = QMaterialManagementDetail.materialManagementDetail;
    private final QOutsourcingCompany outsourcingCompany = QOutsourcingCompany.outsourcingCompany;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;

    private final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", materialManagement.id,
            "deliveryDate", materialManagement.deliveryDate,
            "createdAt", materialManagement.createdAt,
            "updatedAt", materialManagement.updatedAt);

    @Override
    public Page<MaterialManagementResponse> findAll(final MaterialManagementListRequest request,
            final Pageable pageable) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        // MaterialManagementDetail을 기준으로 조회하되, MaterialManagement 기준으로만 정렬
        final List<MaterialManagementDetail> detailContent = queryFactory
                .selectFrom(materialManagementDetail)
                .innerJoin(materialManagementDetail.materialManagement, materialManagement).fetchJoin()
                .leftJoin(materialManagement.site, site).fetchJoin()
                .leftJoin(materialManagement.siteProcess, siteProcess).fetchJoin()
                .leftJoin(materialManagement.outsourcingCompany, outsourcingCompany).fetchJoin()
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 MaterialManagementDetail 개수 조회 (페이지네이션용)
        final Long totalCount = queryFactory
                .select(materialManagementDetail.count())
                .from(materialManagementDetail)
                .innerJoin(materialManagementDetail.materialManagement, materialManagement)
                .leftJoin(materialManagement.site, site)
                .leftJoin(materialManagement.siteProcess, siteProcess)
                .leftJoin(materialManagement.outsourcingCompany, outsourcingCompany)
                .where(condition)
                .fetchOne();

        final long total = Objects.requireNonNullElse(totalCount, 0L);

        // 각 상세품목에 대해 MaterialManagement 정보와 해당 상세품목을 포함하여 응답 생성
        final List<MaterialManagementResponse> responses = detailContent.stream()
                .map(detail -> MaterialManagementResponse.from(detail.getMaterialManagement(), detail))
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    private BooleanBuilder buildCondition(final MaterialManagementListRequest request) {
        final BooleanBuilder builder = new BooleanBuilder();
        builder.and(materialManagement.deleted.eq(false));

        if (StringUtils.hasText(request.siteName())) {
            builder.and(site.name.containsIgnoreCase(request.siteName().trim()));
        }
        if (StringUtils.hasText(request.processName())) {
            builder.and(siteProcess.name.containsIgnoreCase(request.processName().trim()));
        }
        if (StringUtils.hasText(request.outsourcingCompanyName())) {
            builder.and(outsourcingCompany.name.containsIgnoreCase(request.outsourcingCompanyName().trim()));
        }
        if (StringUtils.hasText(request.materialName())) {
            builder.and(materialManagementDetail.name.containsIgnoreCase(request.materialName().trim()));
        }

        // 시작일 검색 (시작일 이후)
        if (request.deliveryStartDate() != null) {
            final OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.deliveryStartDate());
            builder.and(materialManagement.deliveryDate.goe(dateRange[0]));
        }

        // 종료일 검색 (종료일 이전)
        if (request.deliveryEndDate() != null) {
            final OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.deliveryEndDate());
            builder.and(materialManagement.deliveryDate.lt(dateRange[1]));
        }

        return builder;
    }

    @Override
    public List<MaterialManagementResponse> findAllWithoutPaging(final MaterialManagementListRequest request,
            final Sort sort) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        // MaterialManagementDetail을 기준으로 조회하되, MaterialManagement 기준으로만 정렬
        final List<MaterialManagementDetail> detailContent = queryFactory
                .selectFrom(materialManagementDetail)
                .innerJoin(materialManagementDetail.materialManagement, materialManagement).fetchJoin()
                .leftJoin(materialManagement.site, site).fetchJoin()
                .leftJoin(materialManagement.siteProcess, siteProcess).fetchJoin()
                .leftJoin(materialManagement.outsourcingCompany, outsourcingCompany).fetchJoin()
                .where(condition)
                .orderBy(orders) // MaterialManagement 기준으로만 정렬
                .fetch();

        // 각 상세품목에 대해 MaterialManagement 정보와 해당 상세품목을 포함하여 응답 생성
        final List<MaterialManagementResponse> responses = detailContent.stream()
                .map(detail -> MaterialManagementResponse.from(detail.getMaterialManagement(), detail))
                .toList();

        return responses;
    }

}

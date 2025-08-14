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

import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.QMaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.QMaterialManagementDetail;
import com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompany;
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
    public Page<MaterialManagementResponse> findAll(MaterialManagementListRequest request, Pageable pageable) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        List<MaterialManagement> content = queryFactory
                .selectFrom(materialManagement)
                .distinct()
                .leftJoin(materialManagement.site, site).fetchJoin()
                .leftJoin(materialManagement.siteProcess, siteProcess).fetchJoin()
                .leftJoin(materialManagement.outsourcingCompany, outsourcingCompany).fetchJoin()
                .leftJoin(materialManagement.details, materialManagementDetail)
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(materialManagement.count())
                .from(materialManagement)
                .leftJoin(materialManagement.site, site)
                .leftJoin(materialManagement.siteProcess, siteProcess)
                .leftJoin(materialManagement.outsourcingCompany, outsourcingCompany)
                .leftJoin(materialManagement.details, materialManagementDetail)
                .where(condition)
                .fetchOne();

        long total = Objects.requireNonNullElse(totalCount, 0L);

        List<MaterialManagementResponse> responses = content.stream()
                .map(MaterialManagementResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    private BooleanBuilder buildCondition(MaterialManagementListRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

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
            OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.deliveryStartDate());
            builder.and(materialManagement.deliveryDate.goe(dateRange[0]));

            // 종료일이 없으면 시작일의 끝까지로 설정
            if (request.deliveryEndDate() == null) {
                builder.and(materialManagement.deliveryDate.lt(dateRange[1]));
            }
        }

        // 종료일 검색 (종료일 이전)
        if (request.deliveryEndDate() != null) {
            OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.deliveryEndDate());
            builder.and(materialManagement.deliveryDate.lt(dateRange[1]));
        }

        return builder;
    }

    @Override
    public List<MaterialManagement> findAllWithoutPaging(MaterialManagementListRequest request, Sort sort) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        List<MaterialManagement> content = queryFactory
                .selectFrom(materialManagement)
                .distinct()
                .leftJoin(materialManagement.site, site).fetchJoin()
                .leftJoin(materialManagement.siteProcess, siteProcess).fetchJoin()
                .leftJoin(materialManagement.outsourcingCompany, outsourcingCompany).fetchJoin()
                .leftJoin(materialManagement.details, materialManagementDetail)
                .where(condition)
                .orderBy(orders)
                .fetch();

        return content;
    }

}

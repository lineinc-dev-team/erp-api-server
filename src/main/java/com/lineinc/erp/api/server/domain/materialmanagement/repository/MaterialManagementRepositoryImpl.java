package com.lineinc.erp.api.server.domain.materialmanagement.repository;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.MaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.QMaterialManagement;
import com.lineinc.erp.api.server.domain.materialmanagement.entity.QMaterialManagementDetail;
import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.request.MaterialManagementListRequest;
import com.lineinc.erp.api.server.presentation.v1.materialmanagement.dto.response.MaterialManagementResponse;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class MaterialManagementRepositoryImpl implements MaterialManagementRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QMaterialManagement materialManagement = QMaterialManagement.materialManagement;
    private final QMaterialManagementDetail materialManagementDetail = QMaterialManagementDetail.materialManagementDetail;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;

    private final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", materialManagement.id,
            "deliveryDate", materialManagement.deliveryDate,
            "createdAt", materialManagement.createdAt,
            "updatedAt", materialManagement.updatedAt
    );

    @Override
    public Page<MaterialManagementResponse> findAll(MaterialManagementListRequest request, Pageable pageable) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        List<MaterialManagement> content = queryFactory
                .selectFrom(materialManagement)
                .distinct()
                .leftJoin(materialManagement.site, site).fetchJoin()
                .leftJoin(materialManagement.siteProcess, siteProcess).fetchJoin()
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
        if (StringUtils.hasText(request.materialName())) {
            builder.and(materialManagementDetail.name.containsIgnoreCase(request.materialName().trim()));
        }
        if (request.deliveryStartDate() != null) {
            builder.and(materialManagement.deliveryDate.goe(
                    request.deliveryStartDate()
                            .atStartOfDay()
                            .atOffset(AppConstants.KOREA_ZONE_OFFSET)
            ));
        }
        if (request.deliveryEndDate() != null) {
            builder.and(materialManagement.deliveryDate.lt(
                    request.deliveryEndDate()
                            .plusDays(1)
                            .atStartOfDay()
                            .atOffset(AppConstants.KOREA_ZONE_OFFSET)
            ));
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
                .leftJoin(materialManagement.details, materialManagementDetail)
                .where(condition)
                .orderBy(orders)
                .fetch();

        return content;
    }

}

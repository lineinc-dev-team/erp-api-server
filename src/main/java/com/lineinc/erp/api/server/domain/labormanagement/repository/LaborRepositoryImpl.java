package com.lineinc.erp.api.server.domain.labormanagement.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.labormanagement.entity.QLabor;
import com.lineinc.erp.api.server.domain.labormanagement.entity.QLaborFile;
import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompany;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request.LaborListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.response.LaborListResponse;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LaborRepositoryImpl implements LaborRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QLabor labor = QLabor.labor;
    private final QOutsourcingCompany outsourcingCompany = QOutsourcingCompany.outsourcingCompany;
    private final QLaborFile laborFile = QLaborFile.laborFile;

    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QLabor.labor.id,
            "name", QLabor.labor.name,
            "type", QLabor.labor.type,
            "workType", QLabor.labor.workType,
            "isHeadOffice", QLabor.labor.isHeadOffice,
            "hireDate", QLabor.labor.hireDate,
            "createdAt", QLabor.labor.createdAt,
            "updatedAt", QLabor.labor.updatedAt);

    @Override
    public Page<LaborListResponse> findAll(LaborListRequest request, Pageable pageable) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        List<com.lineinc.erp.api.server.domain.labormanagement.entity.Labor> content = queryFactory
                .selectFrom(labor)
                .leftJoin(labor.outsourcingCompany, outsourcingCompany).fetchJoin()
                .leftJoin(labor.files, laborFile)
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(labor.count())
                .from(labor)
                .where(condition)
                .fetchOne();
        long total = Objects.requireNonNullElse(totalCount, 0L);

        List<LaborListResponse> responses = content.stream()
                .map(LaborListResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public List<LaborListResponse> findAllWithoutPaging(LaborListRequest request, Sort sort) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        List<com.lineinc.erp.api.server.domain.labormanagement.entity.Labor> content = queryFactory
                .selectFrom(labor)
                .leftJoin(labor.outsourcingCompany, outsourcingCompany).fetchJoin()
                .leftJoin(labor.files, laborFile)
                .where(condition)
                .orderBy(orders)
                .fetch();

        return content.stream()
                .map(LaborListResponse::from)
                .toList();
    }

    private BooleanBuilder buildCondition(LaborListRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (request.type() != null) {
            builder.and(labor.type.eq((request.type())));
        }

        if (StringUtils.hasText(request.typeDescription())) {
            builder.and(labor.typeDescription.containsIgnoreCase(request.typeDescription().trim()));
        }

        if (StringUtils.hasText(request.name())) {
            builder.and(labor.name.containsIgnoreCase(request.name().trim()));
        }

        if (StringUtils.hasText(request.residentNumber())) {
            builder.and(labor.residentNumber.containsIgnoreCase(request.residentNumber().trim()));
        }

        if (request.isHeadOffice() != null) {
            builder.and(labor.isHeadOffice.eq(request.isHeadOffice()));
        }

        if (request.outsourcingCompanyId() != null) {
            if (request.outsourcingCompanyId() == 0) {
                // 0인 경우 본사 인력
                builder.and(labor.isHeadOffice.eq(true));
            } else {
                // 특정 외주업체 소속
                builder.and(labor.outsourcingCompany.id.eq(request.outsourcingCompanyId()));
            }
        }

        if (StringUtils.hasText(request.phoneNumber())) {
            builder.and(labor.phoneNumber.containsIgnoreCase(request.phoneNumber().trim()));
        }

        return builder;
    }
}

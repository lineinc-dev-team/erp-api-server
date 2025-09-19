package com.lineinc.erp.api.server.domain.outsourcingcompany.repository;

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

import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompanyContact;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * OutsourcingCompanyRepositoryCustom의 구현체.
 * 발주처(OutsourcingCompany) 목록 조회 시, QueryDSL을 사용하여 조건 검색 및 페이징 처리.
 */
@Repository
@RequiredArgsConstructor
public class OutsourcingCompanyRepositoryImpl implements OutsourcingCompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QOutsourcingCompany outsourcingCompany = QOutsourcingCompany.outsourcingCompany;
    private final QOutsourcingCompanyContact outsourcingCompanyContact = QOutsourcingCompanyContact.outsourcingCompanyContact;

    // 정렬 필드를 미리 정의하여 정적 매핑. 추후 정렬 기준이 늘어나면 여기에 추가.
    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QOutsourcingCompany.outsourcingCompany.id,
            "name", QOutsourcingCompany.outsourcingCompany.name,
            "createdAt", QOutsourcingCompany.outsourcingCompany.createdAt,
            "updatedAt", QOutsourcingCompany.outsourcingCompany.updatedAt);

    /**
     * OutsourcingCompany 목록을 요청 조건(request)과 Pageable 정보에 따라 조회.
     *
     * @param request  검색 조건 (예: 발주처명)
     * @param pageable 페이징 및 정렬 정보
     * @return OutsourcingCompanyResponse 리스트를 담은 Page 객체
     */
    @Override
    public Page<CompanyResponse> findAll(final OutsourcingCompanyListRequest request, final Pageable pageable) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(
                pageable,
                SORT_FIELDS);

        final List<OutsourcingCompany> content = queryFactory
                .selectFrom(outsourcingCompany)
                .distinct()
                .leftJoin(outsourcingCompany.contacts, outsourcingCompanyContact).fetchJoin()
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리를 별도로 수행 (성능 최적화를 위해 fetchResults 대신 직접 분리)
        final Long totalCount = queryFactory
                .select(outsourcingCompany.count())
                .from(outsourcingCompany)
                .where(condition)
                .fetchOne();
        final long total = Objects.requireNonNullElse(totalCount, 0L);

        final List<CompanyResponse> responses = content.stream()
                .map(CompanyResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public List<OutsourcingCompany> findAllWithoutPaging(final OutsourcingCompanyListRequest request, final Sort sort) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        return queryFactory
                .selectFrom(outsourcingCompany)
                .distinct()
                .leftJoin(outsourcingCompany.contacts, outsourcingCompanyContact).fetchJoin()
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
    private BooleanBuilder buildCondition(final OutsourcingCompanyListRequest request) {
        final BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 데이터만 조회
        builder.and(outsourcingCompany.deleted.eq(false));

        if (StringUtils.hasText(request.name())) {
            builder.and(outsourcingCompany.name.containsIgnoreCase(request.name().trim()));
        }
        if (StringUtils.hasText(request.businessNumber())) {
            builder.and(outsourcingCompany.businessNumber.contains(request.businessNumber().trim()));
        }
        if (StringUtils.hasText(request.ceoName())) {
            builder.and(outsourcingCompany.ceoName.containsIgnoreCase(request.ceoName().trim()));
        }

        if (StringUtils.hasText(request.landlineNumber())) {
            final String number = request.landlineNumber().trim();
            builder.and(outsourcingCompany.landlineNumber.contains(number));
        }
        if (request.isActive() != null) {
            builder.and(outsourcingCompany.isActive.eq(request.isActive()));
        }
        if (request.type() != null) {
            builder.and(outsourcingCompany.type.eq(request.type()));
        }

        if (request.createdStartDate() != null) {
            final OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.createdStartDate());
            builder.and(outsourcingCompany.createdAt.goe(dateRange[0]));
        }

        if (request.createdEndDate() != null) {
            final OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.createdEndDate());
            builder.and(outsourcingCompany.createdAt.lt(dateRange[1]));
        }

        return builder;
    }

}
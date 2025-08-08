package com.lineinc.erp.api.server.domain.outsourcing.repository;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompanyContact;
import com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompanyFile;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request.OutsourcingCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response.CompanyResponse;
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
    private final QOutsourcingCompanyFile outsourcingCompanyFile = QOutsourcingCompanyFile.outsourcingCompanyFile;

    // 정렬 필드를 미리 정의하여 정적 매핑. 추후 정렬 기준이 늘어나면 여기에 추가.
    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QOutsourcingCompany.outsourcingCompany.id,
            "name", QOutsourcingCompany.outsourcingCompany.name,
            "createdAt", QOutsourcingCompany.outsourcingCompany.createdAt,
            "updatedAt", QOutsourcingCompany.outsourcingCompany.updatedAt
    );

    /**
     * OutsourcingCompany 목록을 요청 조건(request)과 Pageable 정보에 따라 조회.
     *
     * @param request  검색 조건 (예: 발주처명)
     * @param pageable 페이징 및 정렬 정보
     * @return OutsourcingCompanyResponse 리스트를 담은 Page 객체
     */
    @Override
    public Page<CompanyResponse> findAll(OutsourcingCompanyListRequest request, Pageable pageable) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(
                pageable,
                SORT_FIELDS
        );

        List<OutsourcingCompany> content = queryFactory
                .selectFrom(outsourcingCompany)
                .distinct()
                .leftJoin(outsourcingCompany.contacts, outsourcingCompanyContact).fetchJoin()
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리를 별도로 수행 (성능 최적화를 위해 fetchResults 대신 직접 분리)
        Long totalCount = queryFactory
                .select(outsourcingCompany.count())
                .from(outsourcingCompany)
                .where(condition)
                .fetchOne();
        long total = Objects.requireNonNullElse(totalCount, 0L);

        List<CompanyResponse> responses = content.stream()
                .map(CompanyResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    @Override
    public List<OutsourcingCompany> findAllWithoutPaging(OutsourcingCompanyListRequest request, Sort sort) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

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
    private BooleanBuilder buildCondition(OutsourcingCompanyListRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

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
            String number = request.landlineNumber().trim();
            builder.and(outsourcingCompany.landlineNumber.contains(number));
        }
        if (request.isActive() != null) {
            builder.and(outsourcingCompany.isActive.eq(request.isActive()));
        }
        if (request.type() != null) {
            builder.and(outsourcingCompany.type.eq(request.type()));
        }

        if (request.createdStartDate() != null) {
            // atOffset(AppConstants.KOREA_ZONE_OFFSET)는 LocalDateTime → OffsetDateTime 변환 (KST 기준)
            // goe는 Greater or Equal의 약자로 '이상' 조건을 뜻함
            builder.and(outsourcingCompany.createdAt.goe(
                    request.createdStartDate()
                            .atStartOfDay()
                            .atOffset(AppConstants.KOREA_ZONE_OFFSET)
            ));
        }

        if (request.createdEndDate() != null) {
            // lt는 Less Than의 약자로 '미만' 조건을 뜻함
            builder.and(outsourcingCompany.createdAt.lt(
                    request.createdEndDate()
                            .plusDays(1)    // endDate에 하루 더해 다음 날 00시로 만듦 (범위 포함 위해)
                            .atStartOfDay()
                            .atOffset(AppConstants.KOREA_ZONE_OFFSET)
            ));
        }

        return builder;
    }


}
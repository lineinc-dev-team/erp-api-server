package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.common.util.PageableUtils;
import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.QClientCompany;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ClientCompanyRepositoryCustom의 구현체.
 * 발주처(ClientCompany) 목록 조회 시, QueryDSL을 사용하여 조건 검색 및 페이징 처리.
 */
@Repository
@RequiredArgsConstructor
public class ClientCompanyRepositoryImpl implements ClientCompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QClientCompany clientCompany = QClientCompany.clientCompany;

    // 정렬 필드를 미리 정의하여 정적 매핑. 추후 정렬 기준이 늘어나면 여기에 추가.
    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QClientCompany.clientCompany.id,
            "name", QClientCompany.clientCompany.name,
            "createdAt", QClientCompany.clientCompany.createdAt,
            "updatedAt", QClientCompany.clientCompany.updatedAt
    );

    /**
     * ClientCompany 목록을 요청 조건(request)과 Pageable 정보에 따라 조회.
     *
     * @param request  검색 조건 (예: 발주처명)
     * @param pageable 페이징 및 정렬 정보
     * @return ClientCompanyResponse 리스트를 담은 Page 객체
     */
    @Override
    public Page<ClientCompanyResponse> findAll(ClientCompanyListRequest request, Pageable pageable) {
        BooleanBuilder condition = buildCondition(request);
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(
                pageable,
                SORT_FIELDS,
                clientCompany.id.desc()
        );

        List<ClientCompany> content = queryFactory
                .selectFrom(clientCompany)
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리를 별도로 수행 (성능 최적화를 위해 fetchResults 대신 직접 분리)
        Long totalCount = queryFactory
                .select(clientCompany.count())
                .from(clientCompany)
                .where(condition)
                .fetchOne();
        long total = Objects.requireNonNullElse(totalCount, 0L);

        List<ClientCompanyResponse> responses = content.stream()
                .map(ClientCompanyResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    /**
     * 검색 조건을 생성하는 메서드.
     *
     * @param request 검색 요청 객체
     * @return BooleanBuilder (QueryDSL 조건 객체)
     */
    private BooleanBuilder buildCondition(ClientCompanyListRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.name())) {
            // 발주처명에 대해 대소문자 구분 없이 부분 일치 검색
            builder.and(clientCompany.name.containsIgnoreCase(request.name().trim()));
        }

        return builder;
    }
}
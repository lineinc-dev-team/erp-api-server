package com.lineinc.erp.api.server.domain.client.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.QClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.QClientCompanyContact;
import com.lineinc.erp.api.server.domain.user.entity.QUser;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.client.dto.response.ClientCompanyResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * ClientCompanyRepositoryCustom의 구현체.
 * 발주처(ClientCompany) 목록 조회 시, QueryDSL을 사용하여 조건 검색 및 페이징 처리.
 */
@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QClientCompany clientCompany = QClientCompany.clientCompany;
    private final QClientCompanyContact clientCompanyContact = QClientCompanyContact.clientCompanyContact;
    private final QUser user = QUser.user;

    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", QClientCompany.clientCompany.id,
            "name", QClientCompany.clientCompany.name,
            "createdAt", QClientCompany.clientCompany.createdAt,
            "updatedAt", QClientCompany.clientCompany.updatedAt);

    @Override
    public Page<ClientCompanyResponse> findAll(
            final ClientCompanyListRequest request,
            final Pageable pageable) {

        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        var query = queryFactory
                .selectFrom(clientCompany)
                .distinct()
                .leftJoin(clientCompany.contacts, clientCompanyContact).fetchJoin()
                .leftJoin(clientCompany.user, user).fetchJoin()
                .where(condition)
                .orderBy(orders);

        // 페이징이 있는 경우에만 offset, limit 적용 (unpaged 제외)
        if (pageable.isPaged()) {
            query = query.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final List<ClientCompany> content = query.fetch();

        // count 쿼리는 페이징이 있을 때만 수행 (성능 최적화)
        long total;
        if (pageable.isPaged()) {
            final Long totalCount = queryFactory
                    .select(clientCompany.count())
                    .from(clientCompany)
                    .where(condition)
                    .fetchOne();
            total = Objects.requireNonNullElse(totalCount, 0L);
        } else {
            total = content.size();
        }

        final List<ClientCompanyResponse> responses = content.stream()
                .map(ClientCompanyResponse::from)
                .toList();

        return PageableUtils.createPage(responses, pageable, total);
    }

    /**
     * 검색 조건을 생성하는 메서드.
     *
     * @param request 검색 요청 객체
     * @return BooleanBuilder (QueryDSL 조건 객체)
     */
    private BooleanBuilder buildCondition(final ClientCompanyListRequest request) {
        final BooleanBuilder builder = new BooleanBuilder();
        builder.and(clientCompany.deleted.eq(false));

        if (StringUtils.hasText(request.name())) {
            builder.and(clientCompany.name.containsIgnoreCase(request.name().trim()));
        }
        if (StringUtils.hasText(request.businessNumber())) {
            builder.and(clientCompany.businessNumber.contains(request.businessNumber().trim()));
        }
        if (StringUtils.hasText(request.ceoName())) {
            builder.and(clientCompany.ceoName.containsIgnoreCase(request.ceoName().trim()));
        }
        if (StringUtils.hasText(request.contactName())) {
            builder.and(clientCompany.contacts.any().isMain.isTrue()
                    .and(clientCompany.contacts.any().name.containsIgnoreCase(request.contactName().trim())));
        }
        if (StringUtils.hasText(request.userName())) {
            builder.and(clientCompany.user.username.containsIgnoreCase(request.userName().trim()));
        }
        if (StringUtils.hasText(request.email())) {
            builder.and(
                    clientCompany.email.containsIgnoreCase(request.email().trim()));
        }
        if (StringUtils.hasText(request.phoneNumber())) {
            builder.and(clientCompany.phoneNumber.contains(request.phoneNumber().trim()));
        }
        if (StringUtils.hasText(request.landlineNumber())) {
            builder.and(clientCompany.landlineNumber.contains(request.landlineNumber().trim()));
        }
        if (Objects.nonNull(request.isActive())) {
            builder.and(clientCompany.isActive.eq(request.isActive()));
        }
        if (Objects.nonNull(request.createdStartDate())) {
            final OffsetDateTime startOfDay = DateTimeFormatUtils.toUtcStartOfDay(request.createdStartDate());
            builder.and(clientCompany.createdAt.goe(startOfDay));
        }
        if (Objects.nonNull(request.createdEndDate())) {
            final OffsetDateTime endOfDay = DateTimeFormatUtils.toUtcEndOfDay(request.createdEndDate());
            builder.and(clientCompany.createdAt.lt(endOfDay));
        }

        return builder;
    }

}
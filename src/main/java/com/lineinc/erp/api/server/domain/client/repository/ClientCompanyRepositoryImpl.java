package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.domain.client.entity.QClientCompany;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyListRequest;
import com.lineinc.erp.api.server.presentation.v1.client.dto.response.ClientCompanyResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ClientCompanyRepositoryImpl implements ClientCompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ClientCompanyResponse> findAll(ClientCompanyListRequest request, Pageable pageable) {
        QClientCompany clientCompany = QClientCompany.clientCompany;

        BooleanBuilder builder = buildConditions(request, clientCompany);

        OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifiers(pageable, clientCompany);

        List<ClientCompany> content = queryFactory
                .selectFrom(clientCompany)
                .where(builder)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(clientCompany.count())
                .from(clientCompany)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(toResponseList(content), pageable, total);
    }

    private BooleanBuilder buildConditions(ClientCompanyListRequest request, QClientCompany clientCompany) {
        BooleanBuilder builder = new BooleanBuilder();

        if (request.name() != null && !request.name().isBlank()) {
            builder.and(clientCompany.name.containsIgnoreCase(request.name().trim()));
        }

        return builder;
    }

    private OrderSpecifier<?>[] buildOrderSpecifiers(Pageable pageable, QClientCompany clientCompany) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[]{clientCompany.id.desc()};
        }

        PathBuilder<ClientCompany> entityPath = new PathBuilder<>(ClientCompany.class, "clientCompany");

        return pageable.getSort().stream()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    String property = order.getProperty();
                    Class<?> type = resolvePropertyType(property);

                    return new OrderSpecifier(direction, (com.querydsl.core.types.Expression<?>) entityPath.get(property, type));
                })
                .toArray(OrderSpecifier[]::new);
    }

    private Class<?> resolvePropertyType(String property) {
        return switch (property) {
            case "id" -> Long.class;
            case "name" -> String.class;
            // 필요한 필드에 따라 확장 가능
            default -> String.class;
        };
    }

    private List<ClientCompanyResponse> toResponseList(List<ClientCompany> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ClientCompanyResponse toResponse(ClientCompany entity) {
        // 기존 변환 로직
        return ClientCompanyResponse.from(entity);
    }
}
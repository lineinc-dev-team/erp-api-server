package com.lineinc.erp.api.server.domain.client.repository;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompany;
import com.lineinc.erp.api.server.presentation.v1.client.dto.request.ClientCompanyListRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.lineinc.erp.api.server.domain.client.entity.QClientCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.dsl.StringPath;
import jakarta.persistence.EntityManager;

import java.util.List;

@Repository
public class ClientCompanyRepositoryImpl implements ClientCompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ClientCompanyRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ClientCompany> search(ClientCompanyListRequest request, Pageable pageable) {
        QClientCompany clientCompany = QClientCompany.clientCompany;

        BooleanExpression nameCond = containsIgnoreCase(clientCompany.name, request.name());

        List<ClientCompany> content = queryFactory
                .selectFrom(clientCompany)
                .where(nameCond)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(clientCompany.id.desc())
                .fetch();

        long total = queryFactory
                .select(clientCompany.count())
                .from(clientCompany)
                .where(nameCond)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }

    private BooleanExpression containsIgnoreCase(StringPath path, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return path.containsIgnoreCase(value);
    }
}
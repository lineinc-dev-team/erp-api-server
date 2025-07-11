package com.lineinc.erp.api.server.domain.role.repository;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.presentation.v1.role.dto.response.RolesResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import com.lineinc.erp.api.server.domain.role.entity.QRole;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<RolesResponse> findAll(Object request, Pageable pageable) {
        List<Role> content = queryFactory
                .selectFrom(QRole.role)
                .orderBy(QRole.role.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(QRole.role.count())
                .from(QRole.role)
                .fetchOne();

        long total = Objects.requireNonNullElse(totalCount, 0L);

        List<RolesResponse> responses = content.stream()
                .map(RolesResponse::from)
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }
}
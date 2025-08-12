package com.lineinc.erp.api.server.domain.outsourcing.repository;

import static com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompany.outsourcingCompany;
import static com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompanyContract.outsourcingCompanyContract;
import static com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompanyContractContact.outsourcingCompanyContractContact;
import static com.lineinc.erp.api.server.domain.site.entity.QSite.site;
import static com.lineinc.erp.api.server.domain.site.entity.QSiteProcess.siteProcess;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.ContractListSearchRequest;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OutsourcingCompanyContractRepositoryImpl implements OutsourcingCompanyContractRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OutsourcingCompanyContract> findBySearchConditions(ContractListSearchRequest searchRequest,
            Pageable pageable) {

        // 검색 조건을 동적으로 구성
        BooleanBuilder whereClause = new BooleanBuilder();

        // 현장명 검색 (부분 일치)
        if (searchRequest.siteName() != null && !searchRequest.siteName().trim().isEmpty()) {
            whereClause.and(site.name.containsIgnoreCase(searchRequest.siteName().trim()));
        }

        // 공정명 검색 (부분 일치)
        if (searchRequest.processName() != null && !searchRequest.processName().trim().isEmpty()) {
            whereClause.and(siteProcess.name.containsIgnoreCase(searchRequest.processName().trim()));
        }

        // 외주업체명 검색 (부분 일치)
        if (searchRequest.companyName() != null && !searchRequest.companyName().trim().isEmpty()) {
            whereClause.and(outsourcingCompany.name.containsIgnoreCase(searchRequest.companyName().trim()));
        }

        // 계약 구분 검색
        if (searchRequest.contractType() != null) {
            whereClause.and(outsourcingCompanyContract.type.eq(searchRequest.contractType()));
        }

        // 계약 상태 검색
        if (searchRequest.contractStatus() != null) {
            whereClause.and(outsourcingCompanyContract.status.eq(searchRequest.contractStatus()));
        }

        // 계약 시작일 검색 (시작일 이후) - LocalDate를 OffsetDateTime으로 변환
        if (searchRequest.contractStartDate() != null) {
            whereClause.and(outsourcingCompanyContract.contractStartDate.goe(
                    DateTimeFormatUtils.toOffsetDateTime(searchRequest.contractStartDate())));
        }

        // 계약 종료일 검색 (종료일 이전) - LocalDate를 OffsetDateTime으로 변환
        if (searchRequest.contractEndDate() != null) {
            whereClause.and(outsourcingCompanyContract.contractEndDate.loe(
                    DateTimeFormatUtils.toOffsetDateTime(searchRequest.contractEndDate()).plusDays(1).minusNanos(1)));
        }

        // 담당자명 검색 (부분 일치)
        if (searchRequest.contactName() != null && !searchRequest.contactName().trim().isEmpty()) {
            whereClause
                    .and(outsourcingCompanyContractContact.name.containsIgnoreCase(searchRequest.contactName().trim()));
        }

        // 기본 쿼리 구성
        JPAQuery<OutsourcingCompanyContract> query = queryFactory
                .selectFrom(outsourcingCompanyContract)
                .leftJoin(outsourcingCompanyContract.outsourcingCompany, outsourcingCompany)
                .leftJoin(outsourcingCompanyContract.site, site)
                .leftJoin(outsourcingCompanyContract.siteProcess, siteProcess)
                .leftJoin(outsourcingCompanyContract.contacts, outsourcingCompanyContractContact)
                .where(whereClause)
                .distinct();

        // 페이징 적용
        List<OutsourcingCompanyContract> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        long total = queryFactory
                .selectFrom(outsourcingCompanyContract)
                .leftJoin(outsourcingCompanyContract.outsourcingCompany, outsourcingCompany)
                .leftJoin(outsourcingCompanyContract.site, site)
                .leftJoin(outsourcingCompanyContract.siteProcess, siteProcess)
                .leftJoin(outsourcingCompanyContract.contacts, outsourcingCompanyContractContact)
                .where(whereClause)
                .distinct()
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }
}

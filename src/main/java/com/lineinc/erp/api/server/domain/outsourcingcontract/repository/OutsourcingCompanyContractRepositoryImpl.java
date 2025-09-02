package com.lineinc.erp.api.server.domain.outsourcingcontract.repository;

import static com.lineinc.erp.api.server.domain.outsourcing.entity.QOutsourcingCompany.outsourcingCompany;
import static com.lineinc.erp.api.server.domain.outsourcingcontract.entity.QOutsourcingCompanyContract.outsourcingCompanyContract;
import static com.lineinc.erp.api.server.domain.outsourcingcontract.entity.QOutsourcingCompanyContractContact.outsourcingCompanyContractContact;
import static com.lineinc.erp.api.server.domain.site.entity.QSite.site;
import static com.lineinc.erp.api.server.domain.site.entity.QSiteProcess.siteProcess;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.ContractListSearchRequest;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OutsourcingCompanyContractRepositoryImpl implements OutsourcingCompanyContractRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 정렬 필드를 미리 정의하여 정적 매핑. 추후 정렬 기준이 늘어나면 여기에 추가.
    private static final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = new HashMap<>();

    static {
        SORT_FIELDS.put("id", outsourcingCompanyContract.id);
        SORT_FIELDS.put("siteName", site.name);
        SORT_FIELDS.put("processName", siteProcess.name);
        SORT_FIELDS.put("companyName", outsourcingCompany.name);
        SORT_FIELDS.put("businessNumber", outsourcingCompany.businessNumber);
        SORT_FIELDS.put("contractType", outsourcingCompanyContract.type);
        SORT_FIELDS.put("contractStatus", outsourcingCompanyContract.status);
        SORT_FIELDS.put("contractAmount", outsourcingCompanyContract.contractAmount);
        SORT_FIELDS.put("contractStartDate", outsourcingCompanyContract.contractStartDate);
        SORT_FIELDS.put("contractEndDate", outsourcingCompanyContract.contractEndDate);
        SORT_FIELDS.put("createdAt", outsourcingCompanyContract.createdAt);
        SORT_FIELDS.put("updatedAt", outsourcingCompanyContract.updatedAt);
    }

    @Override
    public Page<OutsourcingCompanyContract> findBySearchConditions(ContractListSearchRequest searchRequest,
            Pageable pageable) {

        // 검색 조건을 동적으로 구성
        BooleanBuilder whereClause = buildSearchCondition(searchRequest);

        // 기본 쿼리 구성 - files만 fetch join (MultipleBagFetchException 회피), contacts는 배치 페치
        // 사용
        JPAQuery<OutsourcingCompanyContract> query = queryFactory
                .selectFrom(outsourcingCompanyContract)
                .leftJoin(outsourcingCompanyContract.outsourcingCompany, outsourcingCompany).fetchJoin()
                .leftJoin(outsourcingCompanyContract.site, site).fetchJoin()
                .leftJoin(outsourcingCompanyContract.siteProcess, siteProcess).fetchJoin()
                .leftJoin(outsourcingCompanyContract.files).fetchJoin()
                .where(whereClause)
                .distinct(); // files fetch join으로 인한 중복 제거

        // 정렬 적용
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);
        query.orderBy(orders);

        // 페이징 적용
        List<OutsourcingCompanyContract> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        // count 쿼리는 전량 fetch 후 size 계산 대신 count 사용
        Long total = queryFactory
                .select(outsourcingCompanyContract.id.count())
                .from(outsourcingCompanyContract)
                .leftJoin(outsourcingCompanyContract.outsourcingCompany, outsourcingCompany)
                .leftJoin(outsourcingCompanyContract.site, site)
                .leftJoin(outsourcingCompanyContract.siteProcess, siteProcess)
                .where(whereClause)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public List<OutsourcingCompanyContract> findAllWithoutPaging(ContractListSearchRequest searchRequest, Sort sort) {
        // 검색 조건을 동적으로 구성
        BooleanBuilder whereClause = buildSearchCondition(searchRequest);

        // 정렬 적용
        OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        // 기본 쿼리 구성
        return queryFactory
                .selectFrom(outsourcingCompanyContract)
                .leftJoin(outsourcingCompanyContract.outsourcingCompany, outsourcingCompany).fetchJoin()
                .leftJoin(outsourcingCompanyContract.site, site).fetchJoin()
                .leftJoin(outsourcingCompanyContract.siteProcess, siteProcess).fetchJoin()
                .leftJoin(outsourcingCompanyContract.files).fetchJoin()
                .where(whereClause)
                .orderBy(orders)
                .distinct() // files fetch join으로 인한 중복 제거
                .fetch();
    }

    /**
     * 검색 조건을 구성하는 공통 메서드
     */
    private BooleanBuilder buildSearchCondition(ContractListSearchRequest searchRequest) {
        BooleanBuilder whereClause = new BooleanBuilder();

        // 삭제되지 않은 데이터만 조회
        whereClause.and(outsourcingCompanyContract.deleted.eq(false));

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

        // 사업자등록번호 검색 (부분 일치)
        if (searchRequest.businessNumber() != null && !searchRequest.businessNumber().trim().isEmpty()) {
            whereClause
                    .and(outsourcingCompany.businessNumber.containsIgnoreCase(searchRequest.businessNumber().trim()));
        }

        // 계약 구분 검색
        if (searchRequest.contractType() != null) {
            whereClause.and(outsourcingCompanyContract.type.eq(searchRequest.contractType()));
        }

        // 계약 상태 검색
        if (searchRequest.contractStatus() != null) {
            whereClause.and(outsourcingCompanyContract.status.eq(searchRequest.contractStatus()));
        }

        // 계약 시작일 검색 (시작일 이후) - LocalDate를 UTC 범위로 변환
        if (searchRequest.contractStartDate() != null) {
            OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(searchRequest.contractStartDate());
            whereClause.and(outsourcingCompanyContract.contractStartDate.goe(dateRange[0]));
        }

        // 계약 종료일 검색 (종료일 이전) - LocalDate를 UTC 범위로 변환
        if (searchRequest.contractEndDate() != null) {
            OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(searchRequest.contractEndDate());
            whereClause.and(outsourcingCompanyContract.contractEndDate.lt(dateRange[1]));
        }

        // 담당자명 검색 (부분 일치) - exists 서브쿼리로 다대일 중복/distinct 제거 및 페이징 성능 개선
        if (searchRequest.contactName() != null && !searchRequest.contactName().trim().isEmpty()) {
            whereClause.and(
                    JPAExpressions
                            .selectOne()
                            .from(outsourcingCompanyContractContact)
                            .where(
                                    outsourcingCompanyContractContact.outsourcingCompanyContract
                                            .eq(outsourcingCompanyContract)
                                            .and(outsourcingCompanyContractContact.name
                                                    .containsIgnoreCase(searchRequest.contactName().trim())))
                            .exists());
        }

        return whereClause;
    }
}

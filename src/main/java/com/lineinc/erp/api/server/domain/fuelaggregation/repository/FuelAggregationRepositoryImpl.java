package com.lineinc.erp.api.server.domain.fuelaggregation.repository;

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

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.QFuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.QFuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.domain.outsourcingcompany.entity.QOutsourcingCompany;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.QOutsourcingCompanyContractDriver;
import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.QOutsourcingCompanyContractEquipment;
import com.lineinc.erp.api.server.domain.site.entity.QSite;
import com.lineinc.erp.api.server.domain.site.entity.QSiteProcess;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.request.FuelAggregationListRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.fuelaggregation.dto.response.FuelAggregationListResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;
import com.lineinc.erp.api.server.shared.util.PageableUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class FuelAggregationRepositoryImpl implements FuelAggregationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QFuelAggregation fuelAggregation = QFuelAggregation.fuelAggregation;
    private final QFuelInfo fuelInfo = QFuelInfo.fuelInfo;
    private final QOutsourcingCompany outsourcingCompany = QOutsourcingCompany.outsourcingCompany;
    private final QOutsourcingCompanyContractDriver driver = QOutsourcingCompanyContractDriver.outsourcingCompanyContractDriver;
    private final QOutsourcingCompanyContractEquipment equipment = QOutsourcingCompanyContractEquipment.outsourcingCompanyContractEquipment;
    private final QSite site = QSite.site;
    private final QSiteProcess siteProcess = QSiteProcess.siteProcess;

    private final Map<String, ComparableExpressionBase<?>> SORT_FIELDS = Map.of(
            "id", fuelAggregation.id,
            "date", fuelAggregation.date,
            "createdAt", fuelAggregation.createdAt,
            "updatedAt", fuelAggregation.updatedAt);

    @Override
    public Page<FuelAggregationListResponse> findAll(final FuelAggregationListRequest request,
            final Pageable pageable) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(pageable, SORT_FIELDS);

        // FuelInfo를 기준으로 조회하되, FuelAggregation 기준으로만 정렬
        final List<FuelInfo> fuelInfoContent = queryFactory
                .selectFrom(fuelInfo)
                .innerJoin(fuelInfo.fuelAggregation, fuelAggregation).fetchJoin()
                .leftJoin(fuelAggregation.site, site).fetchJoin()
                .leftJoin(fuelAggregation.siteProcess, siteProcess).fetchJoin()
                .leftJoin(fuelInfo.outsourcingCompany, outsourcingCompany).fetchJoin()
                .leftJoin(fuelInfo.driver, driver).fetchJoin()
                .leftJoin(fuelInfo.equipment, equipment).fetchJoin()
                .where(condition)
                .orderBy(orders)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 FuelInfo 개수 조회 (페이지네이션용)
        final Long totalCount = queryFactory
                .select(fuelInfo.count())
                .from(fuelInfo)
                .innerJoin(fuelInfo.fuelAggregation, fuelAggregation)
                .leftJoin(fuelAggregation.site, site)
                .leftJoin(fuelAggregation.siteProcess, siteProcess)
                .leftJoin(fuelInfo.outsourcingCompany, outsourcingCompany)
                .leftJoin(fuelInfo.driver, driver)
                .leftJoin(fuelInfo.equipment, equipment)
                .where(condition)
                .fetchOne();

        final long total = Objects.requireNonNullElse(totalCount, 0L);

        // 각 FuelInfo마다 FuelAggregation 정보와 함께 응답 생성
        final List<FuelAggregationListResponse> responses = fuelInfoContent.stream()
                .map(info -> FuelAggregationListResponse.from(info.getFuelAggregation(), info))
                .toList();

        return new PageImpl<>(responses, pageable, total);
    }

    private BooleanBuilder buildCondition(final FuelAggregationListRequest request) {
        final BooleanBuilder builder = new BooleanBuilder();
        builder.and(fuelAggregation.deleted.eq(false));

        if (StringUtils.hasText(request.siteName())) {
            builder.and(site.name.containsIgnoreCase(request.siteName().trim()));
        }
        if (StringUtils.hasText(request.processName())) {
            builder.and(siteProcess.name.containsIgnoreCase(request.processName().trim()));
        }
        if (StringUtils.hasText(request.outsourcingCompanyName())) {
            builder.and(outsourcingCompany.name.containsIgnoreCase(request.outsourcingCompanyName().trim()));
        }
        if (StringUtils.hasText(request.vehicleNumber())) {
            builder.and(equipment.vehicleNumber.containsIgnoreCase(request.vehicleNumber().trim()));
        }
        if (request.fuelTypes() != null && !request.fuelTypes().isEmpty()) {
            final BooleanBuilder fuelTypeCondition = new BooleanBuilder();
            for (final FuelInfoFuelType fuelType : request.fuelTypes()) {
                fuelTypeCondition.or(fuelInfo.fuelType.eq(fuelType));
            }
            builder.and(fuelTypeCondition);
        }

        // 시작일 검색 (시작일 이후)
        if (request.dateStartDate() != null) {
            final OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.dateStartDate());
            builder.and(fuelAggregation.date.goe(dateRange[0]));
        }

        // 종료일 검색 (종료일 이전)
        if (request.dateEndDate() != null) {
            final OffsetDateTime[] dateRange = DateTimeFormatUtils.getUtcDateRange(request.dateEndDate());
            builder.and(fuelAggregation.date.lt(dateRange[1]));
        }

        return builder;
    }

    @Override
    public List<FuelAggregationListResponse> findAllWithoutPaging(final FuelAggregationListRequest request,
            final Sort sort) {
        final BooleanBuilder condition = buildCondition(request);
        final OrderSpecifier<?>[] orders = PageableUtils.toOrderSpecifiers(sort, SORT_FIELDS);

        // FuelInfo를 기준으로 조회하되, FuelAggregation 기준으로만 정렬
        final List<FuelInfo> fuelInfoContent = queryFactory
                .selectFrom(fuelInfo)
                .innerJoin(fuelInfo.fuelAggregation, fuelAggregation).fetchJoin()
                .leftJoin(fuelAggregation.site, site).fetchJoin()
                .leftJoin(fuelAggregation.siteProcess, siteProcess).fetchJoin()
                .leftJoin(fuelInfo.outsourcingCompany, outsourcingCompany).fetchJoin()
                .leftJoin(fuelInfo.driver, driver).fetchJoin()
                .leftJoin(fuelInfo.equipment, equipment).fetchJoin()
                .where(condition)
                .orderBy(orders)
                .fetch();

        // 각 FuelInfo마다 FuelAggregation 정보와 함께 응답 생성
        return fuelInfoContent.stream()
                .map(info -> FuelAggregationListResponse.from(info.getFuelAggregation(), info))
                .toList();
    }
}

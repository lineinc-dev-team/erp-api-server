package com.lineinc.erp.api.server.domain.aggregation.managementcost.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostKeyMoneyDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostMealFeeOutsourcingCompaniesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse.ManagementCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagementCostAggregationService {

    private final ManagementCostRepository managementCostRepository;

    public ManagementCostAggregationResponse getManagementCostAggregation(
            final ManagementCostAggregationRequest request) {
        final YearMonth ym = YearMonth.parse(request.yearMonth());
        final LocalDate startMonth = ym.atDay(1);
        final LocalDate nextMonthStart = ym.plusMonths(1).atDay(1);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthStart);

        // GROUP BY: 업체, 관리비 항목(itemType)
        final List<ManagementCost> costs = managementCostRepository
                .findBySiteIdAndSiteProcessIdAndPaymentDateLessThanAndDeletedFalse(
                        request.siteId(),
                        request.siteProcessId(),
                        endExclusive);

        // 각 그룹별로 세부 집계 (식대/전도금은 전용 상세 사용, 그 외는 details 사용)
        final Map<GroupKey, GroupBucket> grouped = new HashMap<>();
        for (final ManagementCost mc : costs) {
            final CompanyResponse.CompanySimpleResponse companySimple = mc.getOutsourcingCompany() != null
                    ? CompanyResponse.CompanySimpleResponse.from(mc.getOutsourcingCompany())
                    : null;
            final var itemType = mc.getItemType();
            final GroupKey key = new GroupKey(companySimple, itemType);
            final GroupBucket bucket = grouped.computeIfAbsent(key, unused -> new GroupBucket());

            if (itemType == ManagementCostItemType.MEAL_FEE) {
                for (final ManagementCostMealFeeDetail meal : mc.getMealFeeDetails()) {
                    if (meal != null)
                        bucket.mealFeeDetails.add(meal);
                }
            } else if (itemType == ManagementCostItemType.KEY_MONEY) {
                for (final ManagementCostKeyMoneyDetail km : mc.getKeyMoneyDetails()) {
                    if (km != null)
                        bucket.keyMoneyDetails.add(km);
                }
            } else {
                for (final ManagementCostDetail detail : mc.getDetails()) {
                    if (detail.isDeleted())
                        continue;
                    bucket.details.add(detail);
                }
            }
        }

        final List<ManagementCostAggregationItem> items = new ArrayList<>();
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startMonth);
        for (final var entry : grouped.entrySet()) {
            final GroupKey key = entry.getKey();
            final GroupBucket bucket = entry.getValue();

            // 각 그룹별로 itemTypeDescription(최신) 하나 뽑기
            final String desc = latestDescription(bucket);

            final var agg = toAggregationItem(key, bucket, startInclusive, endExclusive, desc);
            if (agg != null)
                items.add(agg);
        }
        return new ManagementCostAggregationResponse(items);
    }

    public List<CompanyResponse.CompanySimpleResponse> getMealFeeOutsourcingCompanies(
            final ManagementCostMealFeeOutsourcingCompaniesRequest req) {
        final YearMonth ym = YearMonth.parse(req.yearMonth());
        final LocalDate startMonth = ym.atDay(1);
        final LocalDate nextMonthStart = ym.plusMonths(1).atDay(1);
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startMonth);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthStart);

        return managementCostRepository
                .findMealFeeCosts(
                        req.siteId(),
                        req.siteProcessId(),
                        ManagementCostItemType.MEAL_FEE,
                        startInclusive, endExclusive)
                .stream()
                .map(ManagementCost::getOutsourcingCompany)
                .filter(Objects::nonNull)
                .map(CompanyResponse.CompanySimpleResponse::from)
                .distinct()
                .sorted((a, b) -> a.name().compareToIgnoreCase(b.name()))
                .toList();
    }

    private ManagementCostAggregationItem toAggregationItem(
            final GroupKey key,
            final GroupBucket bucket,
            final OffsetDateTime startInclusive,
            final OffsetDateTime endExclusive,
            final String description) {
        if (key.itemType() == ManagementCostItemType.MEAL_FEE) {
            // 식대: 계를 90%/10%로 분리하여 공급가/부가세를 계산
            long prevSupply = 0, prevVat = 0, prevTotal = 0;
            long currSupply = 0, currVat = 0, currTotal = 0;
            for (final var meal : bucket.mealFeeDetails) {
                final ManagementCost mc = meal.getManagementCost();
                if (mc == null || mc.getPaymentDate() == null)
                    continue;
                final OffsetDateTime paymentDate = mc.getPaymentDate();
                final long amount = safe(meal.getAmount());
                final long supply = deriveSupplyFromTotal(amount);
                final long vat = amount - supply;
                if (paymentDate.isBefore(startInclusive)) {
                    prevSupply += supply;
                    prevVat += vat;
                    prevTotal += amount;
                } else if (!paymentDate.isBefore(startInclusive) && paymentDate.isBefore(endExclusive)) {
                    currSupply += supply;
                    currVat += vat;
                    currTotal += amount;
                }
            }
            if (prevTotal + currTotal == 0)
                return null;
            return new ManagementCostAggregationItem(
                    key.company(),
                    key.itemType() != null ? key.itemType().getLabel() : null,
                    key.itemType(),
                    description,
                    new BillingDetail(prevSupply, prevVat, 0, prevTotal),
                    new BillingDetail(currSupply, currVat, 0, currTotal));
        }

        if (key.itemType() == ManagementCostItemType.KEY_MONEY) {
            // 전도금: 부가세/공제 없음. amount를 공급가와 총계에 동일 반영
            long prevSupply = 0, prevTotal = 0;
            long currSupply = 0, currTotal = 0;
            for (final var km : bucket.keyMoneyDetails) {
                final ManagementCost mc = km.getManagementCost();
                if (mc == null || mc.getPaymentDate() == null)
                    continue;
                final OffsetDateTime paymentDate = mc.getPaymentDate();
                final long amount = safe(km.getAmount());
                if (paymentDate.isBefore(startInclusive)) {
                    prevSupply += amount;
                    prevTotal += amount;
                } else if (!paymentDate.isBefore(startInclusive) && paymentDate.isBefore(endExclusive)) {
                    currSupply += amount;
                    currTotal += amount;
                }
            }
            if (prevTotal + currTotal == 0)
                return null;
            return new ManagementCostAggregationItem(
                    key.company(),
                    key.itemType() != null ? key.itemType().getLabel() : null,
                    key.itemType(),
                    description,
                    new BillingDetail(prevSupply, 0, 0, prevTotal),
                    new BillingDetail(currSupply, 0, 0, currTotal));
        }

        // 기본(식대/전도금 제외) 로직: detail의 공급가/부가세/공제 사용
        long prevSupply = 0, prevVat = 0, prevDeduction = 0;
        long currSupply = 0, currVat = 0, currDeduction = 0;
        for (final var detail : bucket.details) {
            final ManagementCost mc = detail.getManagementCost();
            final OffsetDateTime paymentDate = mc.getPaymentDate();
            final long deduction = Boolean.TRUE.equals(detail.getIsDeductible()) ? safe(detail.getTotal()) : 0L;
            if (paymentDate.isBefore(startInclusive)) {
                prevSupply += safe(detail.getSupplyPrice());
                prevVat += safe(detail.getVat());
                prevDeduction += deduction;
            } else if (!paymentDate.isBefore(startInclusive) && paymentDate.isBefore(endExclusive)) {
                currSupply += safe(detail.getSupplyPrice());
                currVat += safe(detail.getVat());
                currDeduction += deduction;
            }
        }
        final long prevTotal = prevSupply + prevVat;
        final long currTotal = currSupply + currVat;
        if (prevSupply + prevVat + prevDeduction + prevTotal + currSupply + currVat + currDeduction + currTotal == 0)
            return null;
        return new ManagementCostAggregationItem(
                key.company(),
                key.itemType() != null ? key.itemType().getLabel() : null,
                key.itemType(),
                description,
                new BillingDetail(prevSupply, prevVat, prevDeduction, prevTotal),
                new BillingDetail(currSupply, currVat, currDeduction, currTotal));
    }

    private static String latestDescription(final GroupBucket bucket) {
        return bucket.allRelatedManagementCosts().stream()
                .filter(Objects::nonNull)
                .sorted((a, b) -> {
                    if (a.getPaymentDate() == null || b.getPaymentDate() == null)
                        return 0;
                    return b.getPaymentDate().compareTo(a.getPaymentDate()); // 최신 paymentDate 우선
                })
                .map(ManagementCost::getItemTypeDescription)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .findFirst()
                .orElse("");
    }

    private static long safe(final Long v) {
        return v != null ? v : 0L;
    }

    // 총액 기준 90%를 공급가로 (내림), 부가세는 차액(약 10%)
    private static long deriveSupplyFromTotal(final long total) {
        if (total <= 0)
            return 0L;
        return (total * 9) / 10; // 내림 처리
    }

    // 업체+itemType 기준
    private record GroupKey(CompanyResponse.CompanySimpleResponse company, ManagementCostItemType itemType) {
    }

    private static final class GroupBucket {
        private final List<ManagementCostDetail> details = new ArrayList<>();
        private final List<ManagementCostMealFeeDetail> mealFeeDetails = new ArrayList<>();
        private final List<ManagementCostKeyMoneyDetail> keyMoneyDetails = new ArrayList<>();

        private List<ManagementCost> allRelatedManagementCosts() {
            final List<ManagementCost> list = new ArrayList<>();
            for (final var d : details) {
                if (d != null && d.getManagementCost() != null)
                    list.add(d.getManagementCost());
            }
            for (final var m : mealFeeDetails) {
                if (m != null && m.getManagementCost() != null)
                    list.add(m.getManagementCost());
            }
            for (final var k : keyMoneyDetails) {
                if (k != null && k.getManagementCost() != null)
                    list.add(k.getManagementCost());
            }
            return list;
        }
    }
}

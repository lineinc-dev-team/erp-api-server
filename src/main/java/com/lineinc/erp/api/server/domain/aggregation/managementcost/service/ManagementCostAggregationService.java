package com.lineinc.erp.api.server.domain.aggregation.managementcost.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostKeyMoneyDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailDirectContract;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailEquipment;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailOutsourcing;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailOutsourcingContract;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.ManagementCostMealFeeOutsourcingCompaniesRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.MealFeeAggregationDetailRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse.BillingDetail;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.ManagementCostAggregationResponse.ManagementCostAggregationItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MealFeeAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MealFeeAggregationDetailResponse.DailyMealFeeUsage;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MealFeeAggregationDetailResponse.MealFeeAggregationDetailItem;
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
            final String description = ManagementCostItemType.ETC.equals(itemType)
                    ? mc.getItemTypeDescription()
                    : null;
            final GroupKey key = new GroupKey(companySimple, itemType, normalizeDescription(description));
            final GroupBucket bucket = grouped.computeIfAbsent(key, unused -> new GroupBucket());

            if (itemType == ManagementCostItemType.MEAL_FEE) {
                for (final ManagementCostMealFeeDetail meal : mc.getMealFeeDetails()) {
                    if (meal == null || meal.isDeleted()) {
                        continue;
                    }
                    bucket.mealFeeDetails.add(meal);
                }
                for (final ManagementCostMealFeeDetailDirectContract directContract : mc
                        .getMealFeeDetailDirectContracts()) {
                    if (directContract == null || directContract.isDeleted()) {
                        continue;
                    }
                    bucket.mealFeeDetailDirectContracts.add(directContract);
                }
                for (final ManagementCostMealFeeDetailOutsourcing outsourcing : mc.getMealFeeDetailOutsourcings()) {
                    if (outsourcing == null || outsourcing.isDeleted()) {
                        continue;
                    }
                    bucket.mealFeeDetailOutsourcings.add(outsourcing);
                }
                for (final ManagementCostMealFeeDetailEquipment equipment : mc.getMealFeeDetailEquipments()) {
                    if (equipment == null || equipment.isDeleted()) {
                        continue;
                    }
                    bucket.mealFeeDetailEquipments.add(equipment);
                }
                for (final ManagementCostMealFeeDetailOutsourcingContract outsourcingContract : mc
                        .getMealFeeDetailOutsourcingContracts()) {
                    if (outsourcingContract == null || outsourcingContract.isDeleted()) {
                        continue;
                    }
                    bucket.mealFeeDetailOutsourcingContracts.add(outsourcingContract);
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
            final String desc = latestDescription(key, bucket);

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

    /**
     * 식대 집계 상세 조회 (일별, 인원별)
     */
    public MealFeeAggregationDetailResponse getMealFeeAggregationDetail(
            final MealFeeAggregationDetailRequest request) {
        final YearMonth ym = YearMonth.parse(request.yearMonth());
        final LocalDate startMonth = ym.atDay(1);
        final LocalDate nextMonthStart = ym.plusMonths(1).atDay(1);

        // 한국 시간 기준으로 날짜 범위를 UTC로 변환
        // 해당 월 1일 00:00 (한국) = 전월 말일 15:00 (UTC)
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startMonth);
        // 다음 월 1일 00:00 (한국) = 해당 월 말일 15:00 (UTC)
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthStart);

        // DB 레벨에서 모든 조건 필터링: siteId, siteProcessId, itemType, outsourcingCompanyId, 날짜
        // 범위
        final List<ManagementCost> mealFeeCosts = managementCostRepository
                .findMealFeeCostsByCompany(
                        request.siteId(),
                        request.siteProcessId(),
                        ManagementCostItemType.MEAL_FEE,
                        request.outsourcingCompanyId(),
                        startInclusive,
                        endExclusive);

        // 인원별로 그룹핑 (이름 + 직종 조합으로 그룹핑)
        final Map<PersonKey, List<ManagementCostMealFeeDetail>> groupedByPerson = mealFeeCosts
                .stream()
                .flatMap(mc -> mc.getMealFeeDetails().stream()
                        .map(meal -> Map.entry(meal, mc)))
                .filter(entry -> {
                    // labor가 있으면 labor 이름 확인, 없으면 meal name 확인
                    final String personName = getPersonName(entry.getKey());
                    return personName != null && !personName.isBlank();
                })
                .collect(Collectors.groupingBy(
                        entry -> new PersonKey(getPersonName(entry.getKey()), getWorkType(entry.getKey())),
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        // 각 인원별로 일별 집계 생성
        final List<MealFeeAggregationDetailItem> items = groupedByPerson.entrySet()
                .stream()
                .map(entry -> createMealFeeDetailItem(entry.getKey().name(), entry.getKey().workType(),
                        entry.getValue(), ym))
                .toList();

        return new MealFeeAggregationDetailResponse(items);
    }

    /**
     * 인원별 식대 집계 상세 항목 생성
     */
    private MealFeeAggregationDetailItem createMealFeeDetailItem(
            final String name,
            final String workType,
            final List<ManagementCostMealFeeDetail> mealFeeDetails,
            final YearMonth ym) {

        // 일별 사용량 초기화 (1일 ~ 31일까지)
        final Map<Integer, DailyMealFeeUsage> dailyUsageMap = new HashMap<>();
        for (int day = 1; day <= 31; day++) {
            dailyUsageMap.put(day, null); // 데이터가 없는 날은 null
        }

        // 각 식대 상세의 날짜에 해당하는 일자에 집계
        for (final ManagementCostMealFeeDetail meal : mealFeeDetails) {
            final ManagementCost mc = meal.getManagementCost();
            if (mc == null || mc.getPaymentDate() == null)
                continue;

            final LocalDate paymentDate = mc.getPaymentDate().atZoneSameInstant(ZoneOffset.of("+09:00"))
                    .toLocalDate();

            // 조회월에 해당하는 날짜만 집계
            if (paymentDate.getYear() == ym.getYear() && paymentDate.getMonthValue() == ym.getMonthValue()) {
                final int day = paymentDate.getDayOfMonth();

                // 해당 일자에 이미 데이터가 있으면 합산
                final DailyMealFeeUsage currentUsage = dailyUsageMap.get(day);
                final int breakfastCount = safeInteger(meal.getBreakfastCount())
                        + (currentUsage != null ? safeInteger(currentUsage.breakfastCount()) : 0);
                final int lunchCount = safeInteger(meal.getLunchCount())
                        + (currentUsage != null ? safeInteger(currentUsage.lunchCount()) : 0);
                final long unitPrice = meal.getUnitPrice() != null ? meal.getUnitPrice()
                        : (currentUsage != null ? currentUsage.unitPrice() : 0L);
                final long amount = safe(meal.getAmount()) + (currentUsage != null ? currentUsage.amount() : 0L);

                dailyUsageMap.put(day, new DailyMealFeeUsage(breakfastCount, lunchCount, unitPrice, amount));
            }
        }

        return new MealFeeAggregationDetailItem(
                workType,
                name,
                dailyUsageMap.get(1), dailyUsageMap.get(2), dailyUsageMap.get(3), dailyUsageMap.get(4),
                dailyUsageMap.get(5), dailyUsageMap.get(6), dailyUsageMap.get(7), dailyUsageMap.get(8),
                dailyUsageMap.get(9), dailyUsageMap.get(10), dailyUsageMap.get(11), dailyUsageMap.get(12),
                dailyUsageMap.get(13), dailyUsageMap.get(14), dailyUsageMap.get(15), dailyUsageMap.get(16),
                dailyUsageMap.get(17), dailyUsageMap.get(18), dailyUsageMap.get(19), dailyUsageMap.get(20),
                dailyUsageMap.get(21), dailyUsageMap.get(22), dailyUsageMap.get(23), dailyUsageMap.get(24),
                dailyUsageMap.get(25), dailyUsageMap.get(26), dailyUsageMap.get(27), dailyUsageMap.get(28),
                dailyUsageMap.get(29), dailyUsageMap.get(30), dailyUsageMap.get(31));
    }

    private static int safeInteger(final Integer v) {
        return v != null ? v : 0;
    }

    /**
     * 인원 이름 결정: labor가 있으면 labor 이름, 없으면 meal name
     */
    private static String getPersonName(final ManagementCostMealFeeDetail meal) {
        if (meal.getLabor() != null && meal.getLabor().getName() != null
                && !meal.getLabor().getName().isBlank()) {
            return meal.getLabor().getName();
        }
        return meal.getName();
    }

    /**
     * 직종 결정: workType 필드에서 가져오기
     */
    private static String getWorkType(final ManagementCostMealFeeDetail meal) {
        return meal.getWorkType() != null ? meal.getWorkType() : "";
    }

    /**
     * 인원 그룹핑 키 (이름 + 직종)
     */
    private record PersonKey(String name, String workType) {
    }

    private ManagementCostAggregationItem toAggregationItem(
            final GroupKey key,
            final GroupBucket bucket,
            final OffsetDateTime startInclusive,
            final OffsetDateTime endExclusive,
            final String description) {
        if (key.itemType() == ManagementCostItemType.MEAL_FEE) {
            // 식대: 계를 90%/10%로 분리하여 공급가/부가세를 계산
            final Totals previousTotals = new Totals();
            final Totals currentTotals = new Totals();

            for (final var meal : bucket.mealFeeDetails) {
                accumulateMealFee(meal.getManagementCost(), meal.getAmount(), startInclusive, endExclusive,
                        previousTotals, currentTotals);
            }
            for (final var direct : bucket.mealFeeDetailDirectContracts) {
                accumulateMealFee(direct.getManagementCost(), direct.getAmount(), startInclusive, endExclusive,
                        previousTotals, currentTotals);
            }
            for (final var outsourcing : bucket.mealFeeDetailOutsourcings) {
                accumulateMealFee(outsourcing.getManagementCost(), outsourcing.getAmount(), startInclusive,
                        endExclusive, previousTotals, currentTotals);
            }
            for (final var equipment : bucket.mealFeeDetailEquipments) {
                accumulateMealFee(equipment.getManagementCost(), equipment.getAmount(), startInclusive, endExclusive,
                        previousTotals, currentTotals);
            }
            for (final var outsourcingContract : bucket.mealFeeDetailOutsourcingContracts) {
                accumulateMealFee(outsourcingContract.getManagementCost(), outsourcingContract.getAmount(),
                        startInclusive, endExclusive, previousTotals, currentTotals);
            }

            if (previousTotals.total + currentTotals.total == 0) {
                return null;
            }

            return new ManagementCostAggregationItem(
                    key.company(),
                    key.itemType() != null ? key.itemType().getLabel() : null,
                    key.itemType(),
                    description,
                    new BillingDetail(previousTotals.supply, previousTotals.vat, 0, previousTotals.total),
                    new BillingDetail(currentTotals.supply, currentTotals.vat, 0, currentTotals.total));
        }

        if (key.itemType() == ManagementCostItemType.KEY_MONEY) {
            // 전도금: 부가세 없음. amount를 공급가와 총계에 동일 반영, 공제는 isDeductible일 때 amount
            long prevSupply = 0, prevDeduction = 0, prevTotal = 0;
            long currSupply = 0, currDeduction = 0, currTotal = 0;
            for (final var km : bucket.keyMoneyDetails) {
                final ManagementCost mc = km.getManagementCost();
                if (mc == null || mc.getPaymentDate() == null)
                    continue;
                final OffsetDateTime paymentDate = mc.getPaymentDate();
                final long amount = safe(km.getAmount());
                final long deduction = Boolean.TRUE.equals(km.getIsDeductible()) ? amount : 0L;
                if (paymentDate.isBefore(startInclusive)) {
                    prevSupply += amount;
                    prevDeduction += deduction;
                    prevTotal += amount;
                } else if (!paymentDate.isBefore(startInclusive) && paymentDate.isBefore(endExclusive)) {
                    currSupply += amount;
                    currDeduction += deduction;
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
                    new BillingDetail(prevSupply, 0, prevDeduction, prevTotal),
                    new BillingDetail(currSupply, 0, currDeduction, currTotal));
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

    private static String latestDescription(final GroupKey key, final GroupBucket bucket) {
        if (key.itemType() == ManagementCostItemType.ETC
                && key.itemTypeDescription() != null
                && !key.itemTypeDescription().isBlank()) {
            return key.itemTypeDescription();
        }

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

    private static String normalizeDescription(final String description) {
        if (description == null) {
            return "";
        }
        final String trimmed = description.trim();
        return trimmed.isEmpty() ? "" : trimmed;
    }

    private static void accumulateMealFee(
            final ManagementCost managementCost,
            final Long amountValue,
            final OffsetDateTime startInclusive,
            final OffsetDateTime endExclusive,
            final Totals previousTotals,
            final Totals currentTotals) {
        if (managementCost == null || managementCost.getPaymentDate() == null) {
            return;
        }

        final long amount = safe(amountValue);
        if (amount <= 0) {
            return;
        }

        final OffsetDateTime paymentDate = managementCost.getPaymentDate();
        final long supply = deriveSupplyFromTotal(amount);
        final long vat = amount - supply;

        if (paymentDate.isBefore(startInclusive)) {
            previousTotals.supply += supply;
            previousTotals.vat += vat;
            previousTotals.total += amount;
        } else if (!paymentDate.isBefore(startInclusive) && paymentDate.isBefore(endExclusive)) {
            currentTotals.supply += supply;
            currentTotals.vat += vat;
            currentTotals.total += amount;
        }
    }

    private static final class Totals {
        long supply;
        long vat;
        long total;
    }

    // 업체 + itemType + (기타일 때) 항목 설명 기준
    private record GroupKey(
            CompanyResponse.CompanySimpleResponse company,
            ManagementCostItemType itemType,
            String itemTypeDescription) {
    }

    private static final class GroupBucket {
        private final List<ManagementCostDetail> details = new ArrayList<>();
        private final List<ManagementCostMealFeeDetail> mealFeeDetails = new ArrayList<>();
        private final List<ManagementCostMealFeeDetailDirectContract> mealFeeDetailDirectContracts = new ArrayList<>();
        private final List<ManagementCostMealFeeDetailOutsourcing> mealFeeDetailOutsourcings = new ArrayList<>();
        private final List<ManagementCostMealFeeDetailEquipment> mealFeeDetailEquipments = new ArrayList<>();
        private final List<ManagementCostMealFeeDetailOutsourcingContract> mealFeeDetailOutsourcingContracts = new ArrayList<>();
        private final List<ManagementCostKeyMoneyDetail> keyMoneyDetails = new ArrayList<>();

        private List<ManagementCost> allRelatedManagementCosts() {
            final List<ManagementCost> list = new ArrayList<>();
            for (final var detail : details) {
                if (detail != null && detail.getManagementCost() != null) {
                    list.add(detail.getManagementCost());
                }
            }
            for (final var meal : mealFeeDetails) {
                if (meal != null && meal.getManagementCost() != null) {
                    list.add(meal.getManagementCost());
                }
            }
            for (final var direct : mealFeeDetailDirectContracts) {
                if (direct != null && direct.getManagementCost() != null) {
                    list.add(direct.getManagementCost());
                }
            }
            for (final var outsourcing : mealFeeDetailOutsourcings) {
                if (outsourcing != null && outsourcing.getManagementCost() != null) {
                    list.add(outsourcing.getManagementCost());
                }
            }
            for (final var equipment : mealFeeDetailEquipments) {
                if (equipment != null && equipment.getManagementCost() != null) {
                    list.add(equipment.getManagementCost());
                }
            }
            for (final var outsourcingContract : mealFeeDetailOutsourcingContracts) {
                if (outsourcingContract != null && outsourcingContract.getManagementCost() != null) {
                    list.add(outsourcingContract.getManagementCost());
                }
            }
            for (final var keyMoney : keyMoneyDetails) {
                if (keyMoney != null && keyMoney.getManagementCost() != null) {
                    list.add(keyMoney.getManagementCost());
                }
            }
            return list;
        }
    }
}

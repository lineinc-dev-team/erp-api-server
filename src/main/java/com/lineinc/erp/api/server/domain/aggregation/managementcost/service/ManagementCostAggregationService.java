package com.lineinc.erp.api.server.domain.aggregation.managementcost.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.lineinc.erp.api.server.domain.labor.entity.Labor;
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
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.MealFeeAggregationDetailResponse.MealFeeAggregationDetailItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.labor.dto.response.LaborSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse.CompanySimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse.ContractDriverSimpleResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractListResponse.ContractSimpleResponse;
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
            final CompanySimpleResponse companySimple = mc.getOutsourcingCompany() != null
                    ? CompanySimpleResponse.from(mc.getOutsourcingCompany())
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

    public List<CompanySimpleResponse> getMealFeeOutsourcingCompanies(
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
                .map(CompanySimpleResponse::from)
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

        // 카테고리(직원/직영/장비기사/외주업체/외주계약)별로 조회월 일자별 식대 사용량을 누적한다.
        final Map<MealFeeGroupKey, Map<Integer, DailyAccumulator>> groupedByKey = new LinkedHashMap<>();

        for (final ManagementCost cost : mealFeeCosts) {
            final OffsetDateTime paymentDate = cost.getPaymentDate();

            // 1) 직원(사내 인력) 식대
            for (final ManagementCostMealFeeDetail detail : cost.getMealFeeDetails()) {
                if (detail == null || detail.isDeleted()) {
                    continue;
                }
                final String displayName = detail.getLabor() != null && StringUtils.hasText(detail.getLabor().getName())
                        ? detail.getLabor().getName()
                        : detail.getName();
                if (!StringUtils.hasText(displayName)) {
                    continue;
                }

                final LaborSimpleResponse laborSimple = toLaborSimpleResponse(detail.getLabor(),
                        detail.getLabor() != null ? detail.getLabor().getName() : null);
                final MealFeeSubjectData subject = new MealFeeSubjectData(
                        null,
                        null,
                        laborSimple,
                        null,
                        displayName);

                final MealFeeGroupKey key = new MealFeeGroupKey(
                        MealFeeCategory.EMPLOYEE,
                        resolveWorkType(MealFeeCategory.EMPLOYEE, detail.getWorkType()),
                        subject);

                appendMealFeeUsage(groupedByKey, key, paymentDate,
                        detail.getBreakfastCount(), detail.getLunchCount(), detail.getDinnerCount(),
                        detail.getUnitPrice(), detail.getAmount(), ym);
            }

            // 2) 직영 인력 식대
            for (final ManagementCostMealFeeDetailDirectContract detail : cost.getMealFeeDetailDirectContracts()) {
                if (detail == null || detail.isDeleted()) {
                    continue;
                }
                final Labor labor = detail.getLabor();
                final String displayName = labor != null ? labor.getName() : detail.getLaborName();
                if (!StringUtils.hasText(displayName)) {
                    continue;
                }

                final LaborSimpleResponse laborSimple = toLaborSimpleResponse(labor, displayName);
                final MealFeeSubjectData subject = new MealFeeSubjectData(
                        null,
                        null,
                        laborSimple,
                        null,
                        displayName);

                final MealFeeGroupKey key = new MealFeeGroupKey(
                        MealFeeCategory.DIRECT,
                        resolveWorkType(MealFeeCategory.DIRECT, null),
                        subject);

                appendMealFeeUsage(groupedByKey, key, paymentDate,
                        detail.getBreakfastCount(), detail.getLunchCount(), detail.getDinnerCount(),
                        detail.getUnitPrice(), detail.getAmount(), ym);
            }

            // 3) 장비 기사 식대
            for (final ManagementCostMealFeeDetailEquipment detail : cost.getMealFeeDetailEquipments()) {
                if (detail == null || detail.isDeleted()) {
                    continue;
                }
                final var driver = detail.getOutsourcingCompanyContractDriver();
                final String displayName = driver != null ? driver.getName() : null;
                if (!StringUtils.hasText(displayName)) {
                    continue;
                }

                final CompanySimpleResponse companySimple = detail.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(detail.getOutsourcingCompany())
                        : null;
                final ContractDriverSimpleResponse driverSimple = driver != null
                        ? ContractDriverSimpleResponse.from(driver)
                        : null;
                final MealFeeSubjectData subject = new MealFeeSubjectData(
                        companySimple,
                        null,
                        null,
                        driverSimple,
                        displayName);

                final MealFeeGroupKey key = new MealFeeGroupKey(
                        MealFeeCategory.EQUIPMENT,
                        resolveWorkType(MealFeeCategory.EQUIPMENT, null),
                        subject);

                appendMealFeeUsage(groupedByKey, key, paymentDate,
                        detail.getBreakfastCount(), detail.getLunchCount(), detail.getDinnerCount(),
                        detail.getUnitPrice(), detail.getAmount(), ym);
            }

            // 4) 용역업체 식대
            for (final ManagementCostMealFeeDetailOutsourcing detail : cost.getMealFeeDetailOutsourcings()) {
                if (detail == null || detail.isDeleted()) {
                    continue;
                }
                final var company = detail.getOutsourcingCompany();
                final String displayName = company != null ? company.getName() : null;
                if (!StringUtils.hasText(displayName)) {
                    continue;
                }

                final CompanySimpleResponse companySimple = company != null
                        ? CompanySimpleResponse.from(company)
                        : null;
                final MealFeeSubjectData subject = new MealFeeSubjectData(
                        companySimple,
                        null,
                        null,
                        null,
                        displayName);

                final MealFeeGroupKey key = new MealFeeGroupKey(
                        MealFeeCategory.OUTSOURCING_COMPANY,
                        resolveWorkType(MealFeeCategory.OUTSOURCING_COMPANY, null),
                        subject);

                appendMealFeeUsage(groupedByKey, key, paymentDate,
                        detail.getBreakfastCount(), detail.getLunchCount(), detail.getDinnerCount(),
                        detail.getUnitPrice(), detail.getAmount(), ym);
            }

            // 5) 외주계약(외주업체와 계약된 인력) 식대
            for (final ManagementCostMealFeeDetailOutsourcingContract detail : cost
                    .getMealFeeDetailOutsourcingContracts()) {
                if (detail == null || detail.isDeleted()) {
                    continue;
                }

                final var labor = detail.getLabor();
                final var outsourcingContract = labor != null ? labor.getOutsourcingCompanyContract() : null;

                // 외주계약 상세는 연결된 외주업체계약이 없으면 집계 기준이 애매하므로 제외한다.
                if (outsourcingContract == null || !StringUtils.hasText(outsourcingContract.getContractName())) {
                    continue;
                }

                final CompanySimpleResponse companySimple = detail.getOutsourcingCompany() != null
                        ? CompanySimpleResponse.from(detail.getOutsourcingCompany())
                        : null;
                final ContractSimpleResponse contractSimple = ContractSimpleResponse.from(outsourcingContract);
                final LaborSimpleResponse laborSimple = toLaborSimpleResponse(labor, null);

                final MealFeeSubjectData subject = new MealFeeSubjectData(
                        companySimple,
                        contractSimple,
                        laborSimple,
                        null,
                        outsourcingContract.getContractName());

                final MealFeeGroupKey key = new MealFeeGroupKey(
                        MealFeeCategory.OUTSOURCING_CONTRACT,
                        resolveWorkType(MealFeeCategory.OUTSOURCING_CONTRACT, null),
                        subject);

                appendMealFeeUsage(groupedByKey, key, paymentDate,
                        detail.getBreakfastCount(), detail.getLunchCount(), detail.getDinnerCount(),
                        detail.getUnitPrice(), detail.getAmount(), ym);
            }
        }

        final List<MealFeeAggregationDetailItem> items = groupedByKey.entrySet().stream()
                .sorted(MEAL_FEE_GROUP_COMPARATOR)
                .map(entry -> toMealFeeAggregationDetailItem(entry.getKey(), entry.getValue()))
                .toList();

        return new MealFeeAggregationDetailResponse(items);
    }

    private static LaborSimpleResponse toLaborSimpleResponse(final Labor labor, final String fallbackName) {
        if (labor != null) {
            return LaborSimpleResponse.from(labor);
        }
        if (StringUtils.hasText(fallbackName)) {
            return new LaborSimpleResponse(
                    null,
                    fallbackName,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }

    private static void appendMealFeeUsage(
            final Map<MealFeeGroupKey, Map<Integer, DailyAccumulator>> groupedByKey,
            final MealFeeGroupKey key,
            final OffsetDateTime paymentDate,
            final Integer breakfastCount,
            final Integer lunchCount,
            final Integer dinnerCount,
            final Long unitPrice,
            final Long amount,
            final YearMonth ym) {

        if (paymentDate == null || key.subject() == null || !StringUtils.hasText(key.subject().displayName())) {
            return;
        }

        final LocalDate koreaDate = DateTimeFormatUtils.toKoreaLocalDate(paymentDate);
        if (koreaDate.getYear() != ym.getYear() || koreaDate.getMonthValue() != ym.getMonthValue()) {
            return;
        }

        final int day = koreaDate.getDayOfMonth();
        final Map<Integer, DailyAccumulator> dailyMap = groupedByKey.computeIfAbsent(key, unused -> new HashMap<>());
        final DailyAccumulator accumulator = dailyMap.computeIfAbsent(day, unused -> new DailyAccumulator());

        accumulator.addBreakfast(breakfastCount);
        accumulator.addLunch(lunchCount);
        accumulator.addDinner(dinnerCount);
        accumulator.addAmount(amount);
        accumulator.updateUnitPrice(unitPrice);
    }

    private MealFeeAggregationDetailItem toMealFeeAggregationDetailItem(
            final MealFeeGroupKey key,
            final Map<Integer, DailyAccumulator> dailyMap) {
        final MealFeeAggregationDetailResponse.DailyMealFeeUsage[] usages = new MealFeeAggregationDetailResponse.DailyMealFeeUsage[31];
        for (int day = 1; day <= 31; day++) {
            final DailyAccumulator accumulator = dailyMap.get(day);
            usages[day - 1] = accumulator != null ? accumulator.toUsage() : null;
        }

        // DTO는 1~31일까지 고정이므로 배열을 풀어서 생성자에 직접 전달한다.
        return new MealFeeAggregationDetailItem(
                key.workType(),
                key.subject().outsourcingCompany(),
                key.subject().outsourcingCompanyContract(),
                key.subject().labor(),
                key.subject().equipment(),
                usages[0], usages[1], usages[2], usages[3], usages[4], usages[5], usages[6], usages[7],
                usages[8], usages[9], usages[10], usages[11], usages[12], usages[13], usages[14], usages[15],
                usages[16], usages[17], usages[18], usages[19], usages[20], usages[21], usages[22], usages[23],
                usages[24], usages[25], usages[26], usages[27], usages[28], usages[29], usages[30]);
    }

    private static String resolveWorkType(final MealFeeCategory category, final String candidate) {
        if (StringUtils.hasText(candidate)) {
            return candidate;
        }
        return switch (category) {
            case EMPLOYEE -> "직원";
            case DIRECT -> "직영";
            case EQUIPMENT -> "장비";
            case OUTSOURCING_COMPANY -> "용역";
            case OUTSOURCING_CONTRACT -> "외주";
        };
    }

    private enum MealFeeCategory {
        EMPLOYEE(1),
        DIRECT(2),
        EQUIPMENT(3),
        OUTSOURCING_COMPANY(4),
        OUTSOURCING_CONTRACT(5);

        private final int order;

        MealFeeCategory(final int order) {
            this.order = order;
        }

        int order() {
            return order;
        }
    }

    private record MealFeeSubjectData(
            CompanySimpleResponse outsourcingCompany,
            ContractSimpleResponse outsourcingCompanyContract,
            LaborSimpleResponse labor,
            ContractDriverSimpleResponse equipment,
            String displayName) {
    }

    private record MealFeeGroupKey(
            MealFeeCategory category,
            String workType,
            MealFeeSubjectData subject) {
    }

    /**
     * 일별 누적 값을 계산하기 위한 보조 클래스.
     * <p>
     * 하루에 여러 건이 입력될 수 있으므로 조식/중식/금액을 누산하고,
     * 단가는 최초 입력 값을 유지한다.
     * </p>
     */
    private static final class DailyAccumulator {
        private int breakfastCount;
        private int lunchCount;
        private int dinnerCount;
        private long amount;
        private Long unitPrice;

        void addBreakfast(final Integer value) {
            if (value != null) {
                breakfastCount += value;
            }
        }

        void addLunch(final Integer value) {
            if (value != null) {
                lunchCount += value;
            }
        }

        void addDinner(final Integer value) {
            if (value != null) {
                dinnerCount += value;
            }
        }

        void addAmount(final Long value) {
            if (value != null) {
                amount += value;
            }
        }

        void updateUnitPrice(final Long value) {
            if (value != null && unitPrice == null) {
                unitPrice = value;
            }
        }

        MealFeeAggregationDetailResponse.DailyMealFeeUsage toUsage() {
            if (breakfastCount == 0 && lunchCount == 0 && dinnerCount == 0 && amount == 0 && unitPrice == null) {
                return null;
            }

            final Integer breakfast = breakfastCount > 0 ? breakfastCount : null;
            final Integer lunch = lunchCount > 0 ? lunchCount : null;
            final Integer dinner = dinnerCount > 0 ? dinnerCount : null;

            return new MealFeeAggregationDetailResponse.DailyMealFeeUsage(
                    breakfast,
                    lunch,
                    dinner,
                    unitPrice,
                    amount);
        }
    }

    /**
     * 집계 결과의 정렬 기준 (카테고리 → 직종 → 이름).
     */
    private static final Comparator<Map.Entry<MealFeeGroupKey, Map<Integer, DailyAccumulator>>> MEAL_FEE_GROUP_COMPARATOR = Comparator
            .comparing((final Map.Entry<MealFeeGroupKey, Map<Integer, DailyAccumulator>> entry) -> entry.getKey()
                    .category()
                    .order())
            .thenComparing(entry -> entry.getKey().workType(), Comparator.nullsLast(String::compareToIgnoreCase))
            .thenComparing(entry -> entry.getKey().subject() != null ? entry.getKey().subject().displayName() : null,
                    Comparator.nullsLast(String::compareToIgnoreCase));

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
            CompanySimpleResponse company,
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

package com.lineinc.erp.api.server.domain.aggregation.outsourcingcompany.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailOutsourcingContract;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.enums.OutsourcingCompanyContractDefaultDeductionsType;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.DeductionAmountAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.DeductionAmountAggregationResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

/**
 * 외주업체 공제금액 집계 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutsourcingCompanyDeductionAggregationService {

    private final ManagementCostRepository managementCostRepository;

    /**
     * 공제금액 집계 조회
     * 식대, 간식대, 유류대의 전회/금회 공제금액을 반환
     */
    public DeductionAmountAggregationResponse getDeductionAmountAggregation(
            final DeductionAmountAggregationRequest request) {
        final YearMonth ym = YearMonth.parse(request.yearMonth());
        final LocalDate startMonth = ym.atDay(1);
        final LocalDate nextMonthStart = ym.plusMonths(1).atDay(1);
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startMonth);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthStart);

        // 식대 공제금액 집계
        final DeductionAmountAggregationResponse.DeductionDetail mealFee = calculateMealFeeDeduction(
                request.siteId(),
                request.siteProcessId(),
                request.outsourcingCompanyContractId(),
                startInclusive,
                endExclusive);

        // 간식대, 유류대는 추후 구현
        final DeductionAmountAggregationResponse.DeductionDetail snackFee = new DeductionAmountAggregationResponse.DeductionDetail(
                new DeductionAmountAggregationResponse.BillingDetail(null),
                new DeductionAmountAggregationResponse.BillingDetail(null));
        final DeductionAmountAggregationResponse.DeductionDetail fuelFee = new DeductionAmountAggregationResponse.DeductionDetail(
                new DeductionAmountAggregationResponse.BillingDetail(null),
                new DeductionAmountAggregationResponse.BillingDetail(null));

        return new DeductionAmountAggregationResponse(mealFee, snackFee, fuelFee);
    }

    /**
     * 식대 공제금액 집계
     */
    private DeductionAmountAggregationResponse.DeductionDetail calculateMealFeeDeduction(
            final Long siteId,
            final Long siteProcessId,
            final Long outsourcingCompanyContractId,
            final OffsetDateTime startInclusive,
            final OffsetDateTime endExclusive) {
        // 조회월 다음달 1일 미만까지의 모든 식대 관리비 데이터 조회
        // 전회까지의 모든 데이터를 포함하기 위해 paymentDate < endExclusive 조건만 사용
        final List<ManagementCost> allMealFeeCosts = managementCostRepository
                .findBySiteIdAndSiteProcessIdAndPaymentDateLessThanAndDeletedFalse(
                        siteId,
                        siteProcessId,
                        endExclusive);

        // 식대(MEAL_FEE) 타입만 필터링
        final List<ManagementCost> mealFeeCosts = allMealFeeCosts.stream()
                .filter(cost -> cost.getItemType() == ManagementCostItemType.MEAL_FEE)
                .toList();

        long previousAmount = 0L;
        long currentAmount = 0L;

        for (final ManagementCost cost : mealFeeCosts) {
            final OffsetDateTime paymentDate = cost.getPaymentDate();
            if (paymentDate == null) {
                continue;
            }

            // 외주인력 계약 상세 확인
            for (final ManagementCostMealFeeDetailOutsourcingContract detail : cost
                    .getMealFeeDetailOutsourcingContracts()) {
                if (detail == null || detail.isDeleted() || detail.getLabor() == null) {
                    continue;
                }

                // 인력의 계약 확인
                final var contract = detail.getLabor().getOutsourcingCompanyContract();
                if (contract == null) {
                    continue;
                }

                // 요청한 계약 ID와 일치하는지 확인
                if (!contract.getId().equals(outsourcingCompanyContractId)) {
                    continue;
                }

                // 계약의 공제 여부 확인 (defaultDeductions에 MEAL_COST 포함 여부)
                if (!isDeductionEnabled(contract.getDefaultDeductions(),
                        OutsourcingCompanyContractDefaultDeductionsType.MEAL_COST)) {
                    continue;
                }

                // 금액 집계
                final Long amount = detail.getAmount();
                if (amount != null && amount > 0) {
                    if (paymentDate.isBefore(startInclusive)) {
                        previousAmount += amount;
                    } else {
                        currentAmount += amount;
                    }
                }
            }
        }

        return new DeductionAmountAggregationResponse.DeductionDetail(
                new DeductionAmountAggregationResponse.BillingDetail(
                        previousAmount > 0 ? previousAmount : null),
                new DeductionAmountAggregationResponse.BillingDetail(
                        currentAmount > 0 ? currentAmount : null));
    }

    /**
     * 공제 여부 확인
     */
    private boolean isDeductionEnabled(final String defaultDeductions,
            final OutsourcingCompanyContractDefaultDeductionsType deductionType) {
        if (defaultDeductions == null || defaultDeductions.trim().isEmpty()) {
            return false;
        }

        return Arrays.stream(defaultDeductions.split(","))
                .map(String::trim)
                .anyMatch(deduction -> deduction.equals(deductionType.name()));
    }
}

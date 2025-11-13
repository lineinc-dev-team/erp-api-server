package com.lineinc.erp.api.server.domain.aggregation.outsourcingcompany.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfoSubEquipment;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelInfoRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelInfoWithReportDate;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCost;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostDetail;
import com.lineinc.erp.api.server.domain.managementcost.entity.ManagementCostMealFeeDetailOutsourcingContract;
import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.domain.managementcost.repository.ManagementCostRepository;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContract;
import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractEquipment;
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
    private final FuelInfoRepository fuelInfoRepository;

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

        // 간식대 공제금액 집계
        final DeductionAmountAggregationResponse.DeductionDetail snackFee = calculateSnackFeeDeduction(
                request.siteId(),
                request.siteProcessId(),
                request.outsourcingCompanyContractId(),
                startInclusive,
                endExclusive);

        // 유류대 공제금액 집계
        final DeductionAmountAggregationResponse.DeductionDetail fuelFee = calculateFuelDeduction(
                request.siteId(),
                request.siteProcessId(),
                request.outsourcingCompanyContractId(),
                startInclusive,
                endExclusive);

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
     * 간식비 공제금액 집계
     */
    private DeductionAmountAggregationResponse.DeductionDetail calculateSnackFeeDeduction(
            final Long siteId,
            final Long siteProcessId,
            final Long outsourcingCompanyContractId,
            final OffsetDateTime startInclusive,
            final OffsetDateTime endExclusive) {
        // 조회월 다음달 1일 미만까지의 모든 관리비 데이터 조회
        // 전회까지의 모든 데이터를 포함하기 위해 paymentDate < endExclusive 조건만 사용
        final List<ManagementCost> allCosts = managementCostRepository
                .findBySiteIdAndSiteProcessIdAndPaymentDateLessThanAndDeletedFalse(
                        siteId,
                        siteProcessId,
                        endExclusive);

        // 간식비(SNACK_FEE) 타입만 필터링
        final List<ManagementCost> snackFeeCosts = allCosts.stream()
                .filter(cost -> cost.getItemType() == ManagementCostItemType.SNACK_FEE)
                .toList();

        long previousAmount = 0L;
        long currentAmount = 0L;

        for (final ManagementCost cost : snackFeeCosts) {
            final OffsetDateTime paymentDate = cost.getPaymentDate();
            if (paymentDate == null) {
                continue;
            }

            // 공제업체계약 확인
            if (cost.getDeductionCompanyContract() == null) {
                continue;
            }

            // 요청한 계약 ID와 일치하는지 확인
            if (!cost.getDeductionCompanyContract().getId().equals(outsourcingCompanyContractId)) {
                continue;
            }

            // 품목상세의 총합계를 공제금액으로 집계
            for (final ManagementCostDetail detail : cost.getDetails()) {
                if (detail == null || detail.isDeleted()) {
                    continue;
                }

                final Long total = detail.getTotal();
                if (total != null && total > 0) {
                    if (paymentDate.isBefore(startInclusive)) {
                        previousAmount += total;
                    } else {
                        currentAmount += total;
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
     * 유류대 공제금액 집계
     */
    private DeductionAmountAggregationResponse.DeductionDetail calculateFuelDeduction(
            final Long siteId,
            final Long siteProcessId,
            final Long outsourcingCompanyContractId,
            final OffsetDateTime startInclusive,
            final OffsetDateTime endExclusive) {
        final List<FuelInfoWithReportDate> fuelInfos = fuelInfoRepository
                .findBySiteIdAndSiteProcessIdAndReportDateLessThan(siteId, siteProcessId, endExclusive);

        long previousAmount = 0L;
        long currentAmount = 0L;

        for (final FuelInfoWithReportDate fuelInfoWithDate : fuelInfos) {
            final FuelInfo fuelInfo = fuelInfoWithDate.getFuelInfo();
            if (fuelInfo == null) {
                continue;
            }

            final FuelAggregation fuelAggregation = fuelInfo.getFuelAggregation();
            if (fuelAggregation == null) {
                continue;
            }

            final OutsourcingCompanyContractEquipment equipment = fuelInfo.getEquipment();
            if (equipment == null) {
                continue;
            }

            final OutsourcingCompanyContract contract = equipment.getOutsourcingCompanyContract();
            if (contract == null || !contract.getId().equals(outsourcingCompanyContractId)) {
                continue;
            }

            if (!isDeductionEnabled(contract.getDefaultDeductions(),
                    OutsourcingCompanyContractDefaultDeductionsType.FUEL_COST)) {
                continue;
            }

            final long amount = calculateFuelCost(fuelAggregation, fuelInfo);
            if (amount <= 0) {
                continue;
            }

            OffsetDateTime reportDate = fuelInfoWithDate.getReportDate();
            if (reportDate == null) {
                reportDate = fuelAggregation.getDate();
            }

            if (reportDate == null) {
                continue;
            }

            if (reportDate.isBefore(startInclusive)) {
                previousAmount += amount;
            } else if (!reportDate.isBefore(startInclusive) && reportDate.isBefore(endExclusive)) {
                currentAmount += amount;
            }
        }

        return new DeductionAmountAggregationResponse.DeductionDetail(
                new DeductionAmountAggregationResponse.BillingDetail(
                        previousAmount > 0 ? previousAmount : null),
                new DeductionAmountAggregationResponse.BillingDetail(
                        currentAmount > 0 ? currentAmount : null));
    }

    private long calculateFuelCost(final FuelAggregation fuelAggregation, final FuelInfo fuelInfo) {
        long total = 0L;

        if (fuelInfo.getAmount() != null) {
            total += getValueOrZero(fuelInfo.getAmount());
        } else {
            total += calculateFuelCostByType(fuelAggregation, fuelInfo.getFuelType(), fuelInfo.getFuelAmount());
        }

        if (fuelInfo.getSubEquipments() != null) {
            for (final FuelInfoSubEquipment subEquipment : fuelInfo.getSubEquipments()) {
                if (subEquipment == null) {
                    continue;
                }

                if (subEquipment.getAmount() != null) {
                    total += getValueOrZero(subEquipment.getAmount());
                    continue;
                }

                total += calculateFuelCostByType(fuelAggregation, subEquipment.getFuelType(),
                        subEquipment.getFuelAmount());
            }
        }

        return total;
    }

    private long calculateFuelCostByType(
            final FuelAggregation fuelAggregation,
            final FuelInfoFuelType fuelType,
            final Long fuelAmount) {
        if (fuelType == null || fuelAmount == null || fuelAggregation == null) {
            return 0L;
        }

        final long unitPrice = switch (fuelType) {
            case GASOLINE -> getValueOrZero(fuelAggregation.getGasolinePrice());
            case DIESEL -> getValueOrZero(fuelAggregation.getDieselPrice());
            case UREA -> getValueOrZero(fuelAggregation.getUreaPrice());
            default -> 0L;
        };

        return unitPrice * fuelAmount;
    }

    private long getValueOrZero(final Long value) {
        return value != null ? value : 0L;
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

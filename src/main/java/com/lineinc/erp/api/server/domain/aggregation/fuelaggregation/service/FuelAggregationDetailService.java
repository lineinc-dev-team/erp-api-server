package com.lineinc.erp.api.server.domain.aggregation.fuelaggregation.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelInfo;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelInfoFuelType;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.FuelCostAggregationDetailResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.FuelCostAggregationDetailResponse.FuelCostAggregationDetailItem;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.FuelCostAggregationDetailResponse.FuelCostAggregationDetailItem.DailyFuelUsage;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractEquipmentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 유류집계 상세 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuelAggregationDetailService {

    private final FuelAggregationRepository fuelAggregationRepository;
    private final SiteService siteService;
    private final SiteProcessService siteProcessService;

    /**
     * 유류집계 상세 조회 (일별 사용량)
     * 
     * @param siteId        현장 ID
     * @param siteProcessId 공정 ID
     * @param yearMonth     조회월 (YYYY-MM)
     * @param fuelType      유종
     * @return 유류집계 상세 응답
     */
    public FuelCostAggregationDetailResponse getFuelAggregationDetail(
            final Long siteId,
            final Long siteProcessId,
            final String yearMonth,
            final FuelInfoFuelType fuelType) {

        // 현장 조회
        final Site site = siteService.getSiteByIdOrThrow(siteId);

        // 공정 조회
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(siteProcessId);

        // 유류집계 데이터 조회 (해당 월만 포함)
        final YearMonth ym = YearMonth.parse(yearMonth);
        final LocalDate startOfMonth = ym.atDay(1);
        final LocalDate nextMonthFirstDay = ym.plusMonths(1).atDay(1);
        final java.time.OffsetDateTime startInclusive = com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils
                .toUtcStartOfDay(startOfMonth);
        final java.time.OffsetDateTime endExclusive = com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils
                .toUtcStartOfDay(nextMonthFirstDay);
        final List<FuelAggregation> fuelAggregations = fuelAggregationRepository
                .findBySiteAndSiteProcessAndDateGreaterThanEqualAndDateLessThanAndDeletedFalse(
                        site, siteProcess, startInclusive, endExclusive);

        // 장비별로 그룹핑하여 일별 사용량 집계
        final List<FuelCostAggregationDetailItem> items = aggregateFuelByEquipment(
                fuelAggregations, yearMonth, fuelType);

        return new FuelCostAggregationDetailResponse(items);
    }

    /**
     * 장비별로 유류 사용량 집계
     */
    private List<FuelCostAggregationDetailItem> aggregateFuelByEquipment(
            final List<FuelAggregation> fuelAggregations,
            final String yearMonth,
            final FuelInfoFuelType fuelType) {

        final YearMonth ym = YearMonth.parse(yearMonth);
        final int daysInMonth = ym.lengthOfMonth();

        // 유류정보의 업체 + 장비별로 그룹핑
        final Map<CompanyEquipmentKey, List<Map.Entry<FuelAggregation, FuelInfo>>> groupedByCompanyAndEquipment = fuelAggregations
                .stream()
                .flatMap(fa -> fa.getFuelInfos().stream()
                        .filter(fi -> fi.getFuelType() == fuelType) // 지정된 유종만
                        .filter(fi -> fi.getEquipment() != null) // 장비 정보가 있는 경우만
                        .map(fi -> Map.entry(fa, fi)))
                .collect(Collectors.groupingBy(entry -> new CompanyEquipmentKey(
                        entry.getValue().getOutsourcingCompany() != null
                                ? entry.getValue().getOutsourcingCompany().getId()
                                : null,
                        entry.getValue().getEquipment().getId())));

        // 각 장비별로 일별 사용량 집계
        return groupedByCompanyAndEquipment.values().stream()
                .map(group -> createFuelAggregationDetailItem(group, ym, daysInMonth))
                .toList();
    }

    /**
     * 장비별 유류집계 상세 항목 생성
     */
    private FuelCostAggregationDetailItem createFuelAggregationDetailItem(
            final List<Map.Entry<FuelAggregation, FuelInfo>> group,
            final YearMonth ym,
            final int daysInMonth) {

        // 그룹의 첫번째 항목에서 기본 정보 가져오기
        final Map.Entry<FuelAggregation, FuelInfo> firstEntry = group.get(0);
        final FuelInfo firstFuelInfo = firstEntry.getValue();
        final FuelInfoFuelType fuelType = firstFuelInfo.getFuelType();

        // 일별 사용량 및 가격 초기화 (1일 ~ 31일까지)
        final Map<Integer, DailyFuelUsage> dailyUsageMap = new HashMap<>();
        for (int day = 1; day <= 31; day++) {
            dailyUsageMap.put(day, null); // 데이터가 없는 날은 null
        }

        // 각 FuelAggregation의 날짜에 해당하는 일자에 사용량 및 가격 집계
        for (final Map.Entry<FuelAggregation, FuelInfo> entry : group) {
            final FuelAggregation fa = entry.getKey();
            final FuelInfo fi = entry.getValue();

            if (fa.getDate() != null && fi.getFuelAmount() != null) {
                final LocalDate date = fa.getDate().atZoneSameInstant(ZoneOffset.of("+09:00")).toLocalDate();

                // 조회월에 해당하는 날짜만 집계
                if (date.getYear() == ym.getYear() && date.getMonthValue() == ym.getMonthValue()) {
                    final int day = date.getDayOfMonth();

                    // 사용량 합산
                    final DailyFuelUsage currentUsage = dailyUsageMap.get(day);
                    final long currentAmount = currentUsage != null ? currentUsage.amount() : 0L;
                    final long newAmount = currentAmount + fi.getFuelAmount();

                    dailyUsageMap.put(day, new DailyFuelUsage(newAmount));
                }
            }
        }

        return new FuelCostAggregationDetailItem(
                fuelType != null ? fuelType.getLabel() : "",
                fuelType != null ? fuelType.name() : "",
                firstFuelInfo.getOutsourcingCompany() != null
                        ? CompanyResponse.CompanySimpleResponse.from(firstFuelInfo.getOutsourcingCompany())
                        : null,
                firstFuelInfo.getDriver() != null
                        ? ContractDriverResponse.ContractDriverSimpleResponse
                                .from(firstFuelInfo.getDriver())
                        : null,
                firstFuelInfo.getEquipment() != null
                        ? ContractEquipmentResponse.ContractEquipmentSimpleResponse
                                .from(firstFuelInfo.getEquipment())
                        : null,
                dailyUsageMap.get(1), dailyUsageMap.get(2), dailyUsageMap.get(3), dailyUsageMap.get(4),
                dailyUsageMap.get(5), dailyUsageMap.get(6), dailyUsageMap.get(7), dailyUsageMap.get(8),
                dailyUsageMap.get(9), dailyUsageMap.get(10), dailyUsageMap.get(11), dailyUsageMap.get(12),
                dailyUsageMap.get(13), dailyUsageMap.get(14), dailyUsageMap.get(15), dailyUsageMap.get(16),
                dailyUsageMap.get(17), dailyUsageMap.get(18), dailyUsageMap.get(19), dailyUsageMap.get(20),
                dailyUsageMap.get(21), dailyUsageMap.get(22), dailyUsageMap.get(23), dailyUsageMap.get(24),
                dailyUsageMap.get(25), dailyUsageMap.get(26), dailyUsageMap.get(27), dailyUsageMap.get(28),
                dailyUsageMap.get(29), dailyUsageMap.get(30), dailyUsageMap.get(31));
    }

    private record CompanyEquipmentKey(Long companyId, Long equipmentId) {
    }
}

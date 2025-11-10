package com.lineinc.erp.api.server.domain.aggregation.weather.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.fuelaggregation.entity.FuelAggregation;
import com.lineinc.erp.api.server.domain.fuelaggregation.repository.FuelAggregationRepository;
import com.lineinc.erp.api.server.domain.site.entity.Site;
import com.lineinc.erp.api.server.domain.site.entity.SiteProcess;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteProcessService;
import com.lineinc.erp.api.server.domain.site.service.v1.SiteService;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.DailyFuelPriceAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.DailyFuelPriceAggregationResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.DailyFuelPriceAggregationResponse.DailyFuelPrice;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

/**
 * 월별 일자별 유류 가격 집계 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuelPriceAggregationService {

    private final SiteService siteService;
    private final SiteProcessService siteProcessService;
    private final FuelAggregationRepository fuelAggregationRepository;

    /**
     * 현장, 공정, 조회월에 대한 일자별 유종 가격을 반환합니다.
     */
    public DailyFuelPriceAggregationResponse getDailyFuelPrices(final DailyFuelPriceAggregationRequest request) {
        final Site site = siteService.getSiteByIdOrThrow(request.siteId());
        final SiteProcess siteProcess = siteProcessService.getSiteProcessByIdOrThrow(request.siteProcessId());

        // 월 경계 계산 (UTC 기준): [startInclusive, endExclusive)
        final YearMonth ym = YearMonth.parse(request.yearMonth());
        final LocalDate startOfMonth = ym.atDay(1);
        final LocalDate nextMonthFirstDay = ym.plusMonths(1).atDay(1);
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startOfMonth);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthFirstDay);

        final List<FuelAggregation> aggregations = fuelAggregationRepository
                .findBySiteAndSiteProcessAndDateGreaterThanEqualAndDateLessThanAndDeletedFalse(
                        site, siteProcess, startInclusive, endExclusive);

        final DailyFuelPrice[] dailyPrices = new DailyFuelPrice[32];
        aggregations.stream()
                .filter(aggregation -> aggregation.getDate() != null)
                .sorted(Comparator.comparing(FuelAggregation::getDate))
                .forEach(aggregation -> {
                    final int day = DateTimeFormatUtils.toKoreaLocalDate(aggregation.getDate()).getDayOfMonth();
                    dailyPrices[day] = new DailyFuelPrice(
                            aggregation.getGasolinePrice(),
                            aggregation.getDieselPrice(),
                            aggregation.getUreaPrice());
                });

        return new DailyFuelPriceAggregationResponse(
                dailyPrices[1], dailyPrices[2], dailyPrices[3], dailyPrices[4], dailyPrices[5], dailyPrices[6],
                dailyPrices[7], dailyPrices[8], dailyPrices[9], dailyPrices[10], dailyPrices[11], dailyPrices[12],
                dailyPrices[13], dailyPrices[14], dailyPrices[15], dailyPrices[16], dailyPrices[17], dailyPrices[18],
                dailyPrices[19], dailyPrices[20], dailyPrices[21], dailyPrices[22], dailyPrices[23], dailyPrices[24],
                dailyPrices[25], dailyPrices[26], dailyPrices[27], dailyPrices[28], dailyPrices[29], dailyPrices[30],
                dailyPrices[31]);
    }
}

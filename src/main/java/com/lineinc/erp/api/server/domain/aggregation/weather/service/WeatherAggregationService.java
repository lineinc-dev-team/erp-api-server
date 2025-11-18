package com.lineinc.erp.api.server.domain.aggregation.weather.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.dailyreport.entity.DailyReport;
import com.lineinc.erp.api.server.domain.dailyreport.enums.DailyReportStatus;
import com.lineinc.erp.api.server.domain.dailyreport.repository.DailyReportRepository;
import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.request.DailyWeatherAggregationRequest;
import com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response.DailyWeatherAggregationResponse;
import com.lineinc.erp.api.server.shared.util.DateTimeFormatUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherAggregationService {

    private final DailyReportRepository dailyReportRepository;

    /**
     * 현장, 공정, 조회월에 대한 일자별 날씨를 반환합니다.
     */
    public DailyWeatherAggregationResponse getDailyWeather(final DailyWeatherAggregationRequest request) {
        // 월 경계 계산 (UTC 기준): [startInclusive, endExclusive)
        final YearMonth ym = YearMonth.parse(request.yearMonth());
        final LocalDate startOfMonth = ym.atDay(1);
        final LocalDate nextMonthFirstDay = ym.plusMonths(1).atDay(1);
        final OffsetDateTime startInclusive = DateTimeFormatUtils.toUtcStartOfDay(startOfMonth);
        final OffsetDateTime endExclusive = DateTimeFormatUtils.toUtcStartOfDay(nextMonthFirstDay);

        // 월 구간 내 출역일보 조회 (마감 상태만)
        final List<DailyReport> reports = dailyReportRepository
                .findBySiteIdAndSiteProcessIdAndReportDateBetweenMonth(request.siteId(), request.siteProcessId(),
                        startInclusive,
                        endExclusive,
                        List.of(DailyReportStatus.COMPLETED, DailyReportStatus.AUTO_COMPLETED));

        // 일자별 날씨 매핑 (동일 일자의 마지막 값을 사용) - KST 기준 일자 계산
        final Map<Integer, FuelAggregationWeatherType> byDay = new HashMap<>();
        for (final DailyReport dr : reports) {
            if (dr.getReportDate() == null) {
                continue;
            }
            final int day = DateTimeFormatUtils.toKoreaLocalDate(dr.getReportDate()).getDayOfMonth();
            byDay.put(day, dr.getWeather());
        }

        // 1~31일까지 채워서 반환 (존재하지 않는 일자는 null)
        final FuelAggregationWeatherType[] d = new FuelAggregationWeatherType[32];
        for (int i = 1; i <= 31; i++) {
            d[i] = byDay.getOrDefault(i, null);
        }

        return new DailyWeatherAggregationResponse(
                d[1], d[2], d[3], d[4], d[5], d[6], d[7], d[8], d[9], d[10],
                d[11], d[12], d[13], d[14], d[15], d[16], d[17], d[18], d[19], d[20],
                d[21], d[22], d[23], d[24], d[25], d[26], d[27], d[28], d[29], d[30], d[31]);
    }
}

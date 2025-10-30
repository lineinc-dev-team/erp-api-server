package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import com.lineinc.erp.api.server.domain.fuelaggregation.enums.FuelAggregationWeatherType;

/**
 * 월별 일자별 날씨 응답 (day01 ~ day31)
 */
public record DailyWeatherAggregationResponse(
        FuelAggregationWeatherType day01,
        FuelAggregationWeatherType day02,
        FuelAggregationWeatherType day03,
        FuelAggregationWeatherType day04,
        FuelAggregationWeatherType day05,
        FuelAggregationWeatherType day06,
        FuelAggregationWeatherType day07,
        FuelAggregationWeatherType day08,
        FuelAggregationWeatherType day09,
        FuelAggregationWeatherType day10,
        FuelAggregationWeatherType day11,
        FuelAggregationWeatherType day12,
        FuelAggregationWeatherType day13,
        FuelAggregationWeatherType day14,
        FuelAggregationWeatherType day15,
        FuelAggregationWeatherType day16,
        FuelAggregationWeatherType day17,
        FuelAggregationWeatherType day18,
        FuelAggregationWeatherType day19,
        FuelAggregationWeatherType day20,
        FuelAggregationWeatherType day21,
        FuelAggregationWeatherType day22,
        FuelAggregationWeatherType day23,
        FuelAggregationWeatherType day24,
        FuelAggregationWeatherType day25,
        FuelAggregationWeatherType day26,
        FuelAggregationWeatherType day27,
        FuelAggregationWeatherType day28,
        FuelAggregationWeatherType day29,
        FuelAggregationWeatherType day30,
        FuelAggregationWeatherType day31) {
}

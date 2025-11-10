package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 월별 일자별 유종별 가격 응답 (day01 ~ day31)
 */
@Schema(description = "월별 일자별 유종별 가격 응답")
public record DailyFuelPriceAggregationResponse(
        DailyFuelPrice day01,
        DailyFuelPrice day02,
        DailyFuelPrice day03,
        DailyFuelPrice day04,
        DailyFuelPrice day05,
        DailyFuelPrice day06,
        DailyFuelPrice day07,
        DailyFuelPrice day08,
        DailyFuelPrice day09,
        DailyFuelPrice day10,
        DailyFuelPrice day11,
        DailyFuelPrice day12,
        DailyFuelPrice day13,
        DailyFuelPrice day14,
        DailyFuelPrice day15,
        DailyFuelPrice day16,
        DailyFuelPrice day17,
        DailyFuelPrice day18,
        DailyFuelPrice day19,
        DailyFuelPrice day20,
        DailyFuelPrice day21,
        DailyFuelPrice day22,
        DailyFuelPrice day23,
        DailyFuelPrice day24,
        DailyFuelPrice day25,
        DailyFuelPrice day26,
        DailyFuelPrice day27,
        DailyFuelPrice day28,
        DailyFuelPrice day29,
        DailyFuelPrice day30,
        DailyFuelPrice day31) {

    /**
     * 일자별 유종 가격 정보
     */
    @Schema(description = "일자별 유종 가격 정보")
    public record DailyFuelPrice(
            @Schema(description = "휘발유 가격", example = "1650") Long gasolinePrice,
            @Schema(description = "경유 가격", example = "1550") Long dieselPrice,
            @Schema(description = "요소수 가격", example = "1200") Long ureaPrice) {
    }
}

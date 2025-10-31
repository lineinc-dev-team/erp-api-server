package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 식대 집계 상세 응답
 */
@Schema(description = "식대 집계 상세 응답")
public record MealFeeAggregationDetailResponse(
        @Schema(description = "식대 집계 항목 목록") List<MealFeeAggregationDetailItem> items) {

    /**
     * 식대 집계 상세 항목 (인원별)
     */
    @Schema(description = "식대 집계 상세 항목")
    public record MealFeeAggregationDetailItem(
            @Schema(description = "직종", example = "기사") String workType,
            @Schema(description = "이름", example = "홍길동") String name,
            @Schema(description = "1일") DailyMealFeeUsage day01,
            @Schema(description = "2일") DailyMealFeeUsage day02,
            @Schema(description = "3일") DailyMealFeeUsage day03,
            @Schema(description = "4일") DailyMealFeeUsage day04,
            @Schema(description = "5일") DailyMealFeeUsage day05,
            @Schema(description = "6일") DailyMealFeeUsage day06,
            @Schema(description = "7일") DailyMealFeeUsage day07,
            @Schema(description = "8일") DailyMealFeeUsage day08,
            @Schema(description = "9일") DailyMealFeeUsage day09,
            @Schema(description = "10일") DailyMealFeeUsage day10,
            @Schema(description = "11일") DailyMealFeeUsage day11,
            @Schema(description = "12일") DailyMealFeeUsage day12,
            @Schema(description = "13일") DailyMealFeeUsage day13,
            @Schema(description = "14일") DailyMealFeeUsage day14,
            @Schema(description = "15일") DailyMealFeeUsage day15,
            @Schema(description = "16일") DailyMealFeeUsage day16,
            @Schema(description = "17일") DailyMealFeeUsage day17,
            @Schema(description = "18일") DailyMealFeeUsage day18,
            @Schema(description = "19일") DailyMealFeeUsage day19,
            @Schema(description = "20일") DailyMealFeeUsage day20,
            @Schema(description = "21일") DailyMealFeeUsage day21,
            @Schema(description = "22일") DailyMealFeeUsage day22,
            @Schema(description = "23일") DailyMealFeeUsage day23,
            @Schema(description = "24일") DailyMealFeeUsage day24,
            @Schema(description = "25일") DailyMealFeeUsage day25,
            @Schema(description = "26일") DailyMealFeeUsage day26,
            @Schema(description = "27일") DailyMealFeeUsage day27,
            @Schema(description = "28일") DailyMealFeeUsage day28,
            @Schema(description = "29일") DailyMealFeeUsage day29,
            @Schema(description = "30일") DailyMealFeeUsage day30,
            @Schema(description = "31일") DailyMealFeeUsage day31) {
    }

    /**
     * 일별 식대 사용 정보
     */
    @Schema(description = "일별 식대 사용 정보")
    public record DailyMealFeeUsage(
            @Schema(description = "조식 횟수", example = "1") Integer breakfastCount,
            @Schema(description = "중식 횟수", example = "1") Integer lunchCount,
            @Schema(description = "단가 (원)", example = "5000") Long unitPrice,
            @Schema(description = "금액 (원)", example = "10000") Long amount) {
    }
}

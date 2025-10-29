package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractEquipmentResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 유류집계 상세 응답
 */
@Schema(description = "유류집계 상세 응답")
public record FuelCostAggregationDetailResponse(
        @Schema(description = "유류집계 항목 목록") List<FuelCostAggregationDetailItem> items) {

    /**
     * 유류집계 상세 항목
     */
    @Schema(description = "유류집계 상세 항목")
    public record FuelCostAggregationDetailItem(
            @Schema(description = "유종", example = "경유") String fuelType,
            @Schema(description = "유종 코드", example = "DIESEL") String fuelTypeCode,
            @Schema(description = "업체 간단 응답", example = "ABC유류") CompanyResponse.CompanySimpleResponse outsourcingCompany,
            @Schema(description = "기사 간단 응답", example = "홍길동") ContractDriverResponse.ContractDriverSimpleResponse outsourcingCompanyDriver,
            @Schema(description = "장비 간단 응답", example = "굴삭기") ContractEquipmentResponse.ContractEquipmentSimpleResponse outsourcingCompanyEquipment,
            @Schema(description = "1일") DailyFuelUsage day01,
            @Schema(description = "2일") DailyFuelUsage day02,
            @Schema(description = "3일") DailyFuelUsage day03,
            @Schema(description = "4일") DailyFuelUsage day04,
            @Schema(description = "5일") DailyFuelUsage day05,
            @Schema(description = "6일") DailyFuelUsage day06,
            @Schema(description = "7일") DailyFuelUsage day07,
            @Schema(description = "8일") DailyFuelUsage day08,
            @Schema(description = "9일") DailyFuelUsage day09,
            @Schema(description = "10일") DailyFuelUsage day10,
            @Schema(description = "11일") DailyFuelUsage day11,
            @Schema(description = "12일") DailyFuelUsage day12,
            @Schema(description = "13일") DailyFuelUsage day13,
            @Schema(description = "14일") DailyFuelUsage day14,
            @Schema(description = "15일") DailyFuelUsage day15,
            @Schema(description = "16일") DailyFuelUsage day16,
            @Schema(description = "17일") DailyFuelUsage day17,
            @Schema(description = "18일") DailyFuelUsage day18,
            @Schema(description = "19일") DailyFuelUsage day19,
            @Schema(description = "20일") DailyFuelUsage day20,
            @Schema(description = "21일") DailyFuelUsage day21,
            @Schema(description = "22일") DailyFuelUsage day22,
            @Schema(description = "23일") DailyFuelUsage day23,
            @Schema(description = "24일") DailyFuelUsage day24,
            @Schema(description = "25일") DailyFuelUsage day25,
            @Schema(description = "26일") DailyFuelUsage day26,
            @Schema(description = "27일") DailyFuelUsage day27,
            @Schema(description = "28일") DailyFuelUsage day28,
            @Schema(description = "29일") DailyFuelUsage day29,
            @Schema(description = "30일") DailyFuelUsage day30,
            @Schema(description = "31일") DailyFuelUsage day31) {
        public static FuelCostAggregationDetailItem from(final FuelCostAggregationDetailItem item) {
            return new FuelCostAggregationDetailItem(
                    item.fuelType(),
                    item.fuelTypeCode(),
                    item.outsourcingCompany(),
                    item.outsourcingCompanyDriver(),
                    item.outsourcingCompanyEquipment(),
                    item.day01(),
                    item.day02(),
                    item.day03(),
                    item.day04(),
                    item.day05(),
                    item.day06(),
                    item.day07(),
                    item.day08(),
                    item.day09(),
                    item.day10(),
                    item.day11(),
                    item.day12(),
                    item.day13(),
                    item.day14(),
                    item.day15(),
                    item.day16(),
                    item.day17(),
                    item.day18(),
                    item.day19(),
                    item.day20(),
                    item.day21(),
                    item.day22(),
                    item.day23(),
                    item.day24(),
                    item.day25(),
                    item.day26(),
                    item.day27(),
                    item.day28(),
                    item.day29(),
                    item.day30(),
                    item.day31());
        }

        /**
         * 일별 유류 사용량 및 가격
         */
        @Schema(description = "일별 유류 사용량 및 가격")
        public record DailyFuelUsage(
                @Schema(description = "사용량 (리터)", example = "100") Long amount,
                @Schema(description = "단가 (원/리터)", example = "1500") Long price) {
        }
    }
}

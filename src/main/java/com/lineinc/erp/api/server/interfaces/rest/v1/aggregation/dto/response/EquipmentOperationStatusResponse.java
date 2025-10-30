package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response.ContractDriverResponse.ContractDriverSimpleResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 장비가동현황 응답
 */
@Schema(description = "장비가동현황 응답")
public record EquipmentOperationStatusResponse(
        @Schema(description = "장비가동현황 목록") List<EquipmentOperationStatusItem> items) {

    /**
     * 장비가동현황 항목 (외주업체 + 규격별, 1:1 매핑)
     */
    @Schema(description = "장비가동현황 항목")
    public record EquipmentOperationStatusItem(
            @Schema(description = "외주업체 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,
            @Schema(description = "장비 규격", example = "25톤") String specification,
            @Schema(description = "기사 정보") ContractDriverSimpleResponse driver,
            @Schema(description = "장비 정보") EquipmentDailyUsage equipment,
            @Schema(description = "유류 사용량") FuelUsage fuel,
            @Schema(description = "서브장비 구분값 목록") List<SubEquipmentTypeItem> subEquipments) {
    }

    /**
     * 장비 일별 사용량
     */
    @Schema(description = "장비 일별 사용량")
    public record EquipmentDailyUsage(
            @Schema(description = "구분값", example = "월대") String type,
            @Schema(description = "구분값 코드", example = "MONTHLY") String typeCode,
            @Schema(description = "1일") DailyUsage day01,
            @Schema(description = "2일") DailyUsage day02,
            @Schema(description = "3일") DailyUsage day03,
            @Schema(description = "4일") DailyUsage day04,
            @Schema(description = "5일") DailyUsage day05,
            @Schema(description = "6일") DailyUsage day06,
            @Schema(description = "7일") DailyUsage day07,
            @Schema(description = "8일") DailyUsage day08,
            @Schema(description = "9일") DailyUsage day09,
            @Schema(description = "10일") DailyUsage day10,
            @Schema(description = "11일") DailyUsage day11,
            @Schema(description = "12일") DailyUsage day12,
            @Schema(description = "13일") DailyUsage day13,
            @Schema(description = "14일") DailyUsage day14,
            @Schema(description = "15일") DailyUsage day15,
            @Schema(description = "16일") DailyUsage day16,
            @Schema(description = "17일") DailyUsage day17,
            @Schema(description = "18일") DailyUsage day18,
            @Schema(description = "19일") DailyUsage day19,
            @Schema(description = "20일") DailyUsage day20,
            @Schema(description = "21일") DailyUsage day21,
            @Schema(description = "22일") DailyUsage day22,
            @Schema(description = "23일") DailyUsage day23,
            @Schema(description = "24일") DailyUsage day24,
            @Schema(description = "25일") DailyUsage day25,
            @Schema(description = "26일") DailyUsage day26,
            @Schema(description = "27일") DailyUsage day27,
            @Schema(description = "28일") DailyUsage day28,
            @Schema(description = "29일") DailyUsage day29,
            @Schema(description = "30일") DailyUsage day30,
            @Schema(description = "31일") DailyUsage day31) {
    }

    /**
     * 서브장비 구분값 항목 (type)
     */
    @Schema(description = "서브장비 구분값 항목")
    public record SubEquipmentTypeItem(
            @Schema(description = "구분값", example = "죽통임대") String type,
            @Schema(description = "구분값 코드", example = "PIPE_RENTAL") String typeCode,
            @Schema(description = "1일") DailyUsage day01,
            @Schema(description = "2일") DailyUsage day02,
            @Schema(description = "3일") DailyUsage day03,
            @Schema(description = "4일") DailyUsage day04,
            @Schema(description = "5일") DailyUsage day05,
            @Schema(description = "6일") DailyUsage day06,
            @Schema(description = "7일") DailyUsage day07,
            @Schema(description = "8일") DailyUsage day08,
            @Schema(description = "9일") DailyUsage day09,
            @Schema(description = "10일") DailyUsage day10,
            @Schema(description = "11일") DailyUsage day11,
            @Schema(description = "12일") DailyUsage day12,
            @Schema(description = "13일") DailyUsage day13,
            @Schema(description = "14일") DailyUsage day14,
            @Schema(description = "15일") DailyUsage day15,
            @Schema(description = "16일") DailyUsage day16,
            @Schema(description = "17일") DailyUsage day17,
            @Schema(description = "18일") DailyUsage day18,
            @Schema(description = "19일") DailyUsage day19,
            @Schema(description = "20일") DailyUsage day20,
            @Schema(description = "21일") DailyUsage day21,
            @Schema(description = "22일") DailyUsage day22,
            @Schema(description = "23일") DailyUsage day23,
            @Schema(description = "24일") DailyUsage day24,
            @Schema(description = "25일") DailyUsage day25,
            @Schema(description = "26일") DailyUsage day26,
            @Schema(description = "27일") DailyUsage day27,
            @Schema(description = "28일") DailyUsage day28,
            @Schema(description = "29일") DailyUsage day29,
            @Schema(description = "30일") DailyUsage day30,
            @Schema(description = "31일") DailyUsage day31) {
    }

    /**
     * 일별 사용량 (시간 및 단가)
     */
    @Schema(description = "일별 사용량")
    public record DailyUsage(
            @Schema(description = "시간", example = "8.0") Double hours,
            @Schema(description = "단가", example = "50000") Long unitPrice) {
    }

    /**
     * 유류 일별 사용량 (amount만 표기)
     */
    @Schema(description = "유류 일별 사용량")
    public record FuelUsage(
            @Schema(description = "1일") FuelDailyUsage day01,
            @Schema(description = "2일") FuelDailyUsage day02,
            @Schema(description = "3일") FuelDailyUsage day03,
            @Schema(description = "4일") FuelDailyUsage day04,
            @Schema(description = "5일") FuelDailyUsage day05,
            @Schema(description = "6일") FuelDailyUsage day06,
            @Schema(description = "7일") FuelDailyUsage day07,
            @Schema(description = "8일") FuelDailyUsage day08,
            @Schema(description = "9일") FuelDailyUsage day09,
            @Schema(description = "10일") FuelDailyUsage day10,
            @Schema(description = "11일") FuelDailyUsage day11,
            @Schema(description = "12일") FuelDailyUsage day12,
            @Schema(description = "13일") FuelDailyUsage day13,
            @Schema(description = "14일") FuelDailyUsage day14,
            @Schema(description = "15일") FuelDailyUsage day15,
            @Schema(description = "16일") FuelDailyUsage day16,
            @Schema(description = "17일") FuelDailyUsage day17,
            @Schema(description = "18일") FuelDailyUsage day18,
            @Schema(description = "19일") FuelDailyUsage day19,
            @Schema(description = "20일") FuelDailyUsage day20,
            @Schema(description = "21일") FuelDailyUsage day21,
            @Schema(description = "22일") FuelDailyUsage day22,
            @Schema(description = "23일") FuelDailyUsage day23,
            @Schema(description = "24일") FuelDailyUsage day24,
            @Schema(description = "25일") FuelDailyUsage day25,
            @Schema(description = "26일") FuelDailyUsage day26,
            @Schema(description = "27일") FuelDailyUsage day27,
            @Schema(description = "28일") FuelDailyUsage day28,
            @Schema(description = "29일") FuelDailyUsage day29,
            @Schema(description = "30일") FuelDailyUsage day30,
            @Schema(description = "31일") FuelDailyUsage day31) {
    }

    @Schema(description = "유류 일별 사용량 단위")
    public record FuelDailyUsage(
            @Schema(description = "사용량(리터)", example = "100") Long amount) {
    }
}

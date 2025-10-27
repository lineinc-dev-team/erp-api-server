package com.lineinc.erp.api.server.interfaces.rest.v1.aggregation.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.response.CompanyResponse;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 재료비 집계 응답
 */
@Schema(description = "재료비 집계 응답")
public record MaterialCostAggregationResponse(
        @Schema(description = "자재관리 목록") List<MaterialManagementItemResponse> materialManagements,
        @Schema(description = "강재수불부 목록") List<SteelManagementItemResponse> steelManagements,
        @Schema(description = "유류집계 목록") List<FuelAggregationItemResponse> fuelAggregations) {

    /**
     * 자재관리 항목 응답
     */
    @Schema(description = "자재관리 항목 응답")
    public record MaterialManagementItemResponse(
            @Schema(description = "외주업체 요약 정보") CompanyResponse.CompanySimpleResponse outsourcingCompany,
            @Schema(description = "품명", example = "시멘트") String itemName,
            @Schema(description = "전회까지 청구내역") BillingDetail previousBilling,
            @Schema(description = "금회 청구내역") BillingDetail currentBilling) {

        /**
         * 청구내역 상세
         */
        @Schema(description = "청구내역 상세")
        public record BillingDetail(
                @Schema(description = "공급가", example = "1000000") Long supplyPrice,
                @Schema(description = "부가세", example = "100000") Long vat,
                @Schema(description = "공제금액", example = "50000") Long deductionAmount,
                @Schema(description = "계", example = "1050000") Long total) {
        }
    }

    /**
     * 강재수불부 항목 응답
     */
    @Schema(description = "강재수불부 항목 응답")
    public record SteelManagementItemResponse(
            @Schema(description = "사업자등록번호", example = "123-45-67890") String businessNumber,
            @Schema(description = "품명", example = "H빔") String itemName,
            @Schema(description = "업체명", example = "DEF철강") String companyName,
            @Schema(description = "대표자", example = "김철수") String representative,
            @Schema(description = "연락처", example = "010-9876-5432") String phoneNumber,
            @Schema(description = "은행", example = "신한은행") String bankName,
            @Schema(description = "계좌번호", example = "987-654-321098") String accountNumber,
            @Schema(description = "계좌명", example = "김철수") String accountHolder) {
    }

    /**
     * 유류집계 항목 응답
     */
    @Schema(description = "유류집계 항목 응답")
    public record FuelAggregationItemResponse(
            @Schema(description = "사업자등록번호", example = "123-45-67890") String businessNumber,
            @Schema(description = "품명", example = "경유") String itemName,
            @Schema(description = "업체명", example = "GHI유통") String companyName,
            @Schema(description = "대표자", example = "이영희") String representative,
            @Schema(description = "연락처", example = "010-5555-6666") String phoneNumber,
            @Schema(description = "은행", example = "하나은행") String bankName,
            @Schema(description = "계좌번호", example = "111-222-333444") String accountNumber,
            @Schema(description = "계좌명", example = "이영희") String accountHolder) {
    }
}

package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;

@Schema(description = "관리비 등록 요청")
public record ManagementCostCreateRequest(
        @Schema(description = "현장 ID", example = "1") Long siteId,

        @Schema(description = "공정 ID", example = "1") Long siteProcessId,

        @Schema(description = "외주 업체 ID", example = "1") Long outsourcingCompanyId,

        @Schema(description = "관리비 항목 구분", example = "DEPOSIT") ItemType itemType,

        @Schema(description = "관리비 항목 설명", example = "6월 전기요금") String itemDescription,

        @Schema(description = "결제일자", example = "2024-07-15") LocalDate paymentDate,

        @Schema(description = "비고", example = "기성 1회차 비용") String memo,

        @Schema(description = "관리비 상세 품목 목록") List<ManagementCostDetailCreateRequest> details,

        @Schema(description = "관리비 전도금 상세 목록") List<ManagementCostKeyMoneyDetailCreateRequest> keyMoneyDetails,

        @Schema(description = "관리비 식대 상세 목록") List<ManagementCostMealFeeDetailCreateRequest> mealFeeDetails,

        @Schema(description = "관리비 파일 목록") List<ManagementCostFileCreateRequest> files,

        @Schema(description = "외주업체 정보 (신규 등록 또는 수정용)") OutsourcingCompanyInfo outsourcingCompanyInfo) {

    @Schema(description = "외주업체 정보")
    public record OutsourcingCompanyInfo(
            @Schema(description = "업체명", example = "삼성건설") String name,

            @Schema(description = "사업자등록번호", example = "123-45-67890") String businessNumber,

            @Schema(description = "대표자명", example = "홍길동") String ceoName,

            @Schema(description = "은행명", example = "신한은행") String bankName,

            @Schema(description = "계좌번호", example = "123-456-789012") String accountNumber,

            @Schema(description = "예금주", example = "홍길동") String accountHolder,

            @Schema(description = "비고", example = "신규 거래처") String memo) {
    }
}

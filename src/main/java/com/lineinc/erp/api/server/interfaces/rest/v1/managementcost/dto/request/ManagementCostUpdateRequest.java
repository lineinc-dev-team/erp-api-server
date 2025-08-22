package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyBasicInfoRequest;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.managementcost.enums.ItemType;

@Schema(description = "관리비 수정 요청")
public record ManagementCostUpdateRequest(
        @Schema(description = "현장 ID", example = "1") Long siteId,

        @Schema(description = "공정 ID", example = "1") Long siteProcessId,

        @Schema(description = "외주 업체 ID", example = "1") Long outsourcingCompanyId,

        @Schema(description = "관리비 항목 구분", example = "DEPOSIT") ItemType itemType,

        @Schema(description = "관리비 항목 설명", example = "6월 전기요금") String itemTypeDescription,

        @Schema(description = "결제일자", example = "2024-07-15") LocalDate paymentDate,

        @Schema(description = "비고", example = "기성 1회차 비용") String memo,

        @Schema(description = "관리비 상세 품목 목록") List<ManagementCostDetailUpdateRequest> details,

        @Schema(description = "관리비 전도금 상세 목록") List<ManagementCostKeyMoneyDetailUpdateRequest> keyMoneyDetails,

        @Schema(description = "관리비 식대 상세 목록") List<ManagementCostMealFeeDetailUpdateRequest> mealFeeDetails,

        @Schema(description = "관리비 파일 목록") List<ManagementCostFileUpdateRequest> files,

        @Schema(description = "외주업체 정보 (신규 등록 또는 수정용)") OutsourcingCompanyBasicInfoRequest outsourcingCompanyInfo,

        @Schema(description = "변경이력 메모 수정 목록") List<ChangeHistoryRequest> changeHistories) {

    @Schema(description = "변경이력 메모 수정 요청")
    public record ChangeHistoryRequest(
            @Schema(description = "변경이력 ID", example = "1") Long id,
            @Schema(description = "메모", example = "수정 사유") String memo) {
    }
}

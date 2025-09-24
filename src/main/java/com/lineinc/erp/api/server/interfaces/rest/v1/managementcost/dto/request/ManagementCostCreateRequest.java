package com.lineinc.erp.api.server.interfaces.rest.v1.managementcost.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.managementcost.enums.ManagementCostItemType;
import com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request.OutsourcingCompanyBasicInfoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "관리비 등록 요청")
public record ManagementCostCreateRequest(
        @Schema(description = "현장 ID", example = "1") @NotNull Long siteId,
        @Schema(description = "공정 ID", example = "1") @NotNull Long siteProcessId,
        @Schema(description = "외주 업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "관리비 항목 구분", example = "DEPOSIT") @NotNull ManagementCostItemType itemType,
        @Schema(description = "관리비 항목 설명", example = "6월 전기요금") String itemTypeDescription,
        @Schema(description = "결제일자", example = "2024-07-15") @NotNull LocalDate paymentDate,
        @Schema(description = "비고", example = "기성 1회차 비용") String memo,
        @Schema(description = "관리비 상세 품목 목록") @Valid List<ManagementCostDetailCreateRequest> details,
        @Schema(description = "관리비 전도금 상세 목록") @Valid List<ManagementCostKeyMoneyDetailCreateRequest> keyMoneyDetails,
        @Schema(description = "관리비 식대 상세 목록") @Valid List<ManagementCostMealFeeDetailCreateRequest> mealFeeDetails,
        @Schema(description = "관리비 파일 목록") @Valid List<ManagementCostFileCreateRequest> files,
        @Schema(description = "외주업체 정보 (신규 등록 또는 수정용)") @Valid OutsourcingCompanyBasicInfoRequest outsourcingCompanyInfo) {
}

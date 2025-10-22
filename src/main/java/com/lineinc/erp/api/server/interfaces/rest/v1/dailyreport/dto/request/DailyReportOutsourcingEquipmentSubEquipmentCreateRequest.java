package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주업체계약 서브 장비 출역일보 등록 요청")
public record DailyReportOutsourcingEquipmentSubEquipmentCreateRequest(
        @Schema(description = "외주업체계약 서브 장비 ID", example = "1") @NotNull Long outsourcingCompanyContractSubEquipmentId,
        @Schema(description = "작업내용", example = "기초공사") String workContent,
        @Schema(description = "단가", example = "100000") @NotNull Long unitPrice,
        @Schema(description = "시간", example = "8.0") @NotNull Double workHours,
        @Schema(description = "비고", example = "특별 지시사항") String memo) {
}

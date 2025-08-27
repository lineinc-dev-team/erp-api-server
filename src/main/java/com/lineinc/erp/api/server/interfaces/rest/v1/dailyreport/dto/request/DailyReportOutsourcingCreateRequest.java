package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "외주 출역일보 등록 요청")
public record DailyReportOutsourcingCreateRequest(
        @NotNull @Schema(description = "업체 ID", example = "1") Long outsourcingCompanyId,

        @NotNull @Schema(description = "외주업체계약 인력 ID", example = "1") Long outsourcingCompanyContractWorkerId,

        @Schema(description = "구분값", example = "기초공사") String category,

        @Schema(description = "작업내용", example = "기초공사") String workContent,

        @Schema(description = "공수", example = "8.0") Double workQuantity,

        @Schema(description = "비고", example = "특별 지시사항") String memo) {
}

package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;

@Schema(description = "출역일보 외주 수정 요청")
public record DailyReportOutsourcingUpdateRequest(
        @Schema(description = "수정할 외주 정보 목록") List<@Valid OutsourcingUpdateInfo> outsourcings) {

    @Schema(description = "외주 정보 수정 내용")
    public record OutsourcingUpdateInfo(
            @Schema(description = "ID", example = "1") Long id,
            @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
            @Schema(description = "외주업체계약 인력 ID", example = "1") Long outsourcingCompanyContractWorkerId,
            @Schema(description = "구분값", example = "토목공사") String category,
            @Schema(description = "작업내용", example = "기초 콘크리트 타설") String workContent,
            @Schema(description = "공수", example = "8.0") Double workQuantity,
            @Schema(description = "비고", example = "오전 작업") String memo) {
    }
}

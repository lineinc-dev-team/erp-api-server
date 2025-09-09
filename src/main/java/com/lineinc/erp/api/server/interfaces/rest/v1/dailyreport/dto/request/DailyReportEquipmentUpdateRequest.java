package com.lineinc.erp.api.server.interfaces.rest.v1.dailyreport.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "출역일보 장비 수정 요청")
public record DailyReportEquipmentUpdateRequest(
        @Schema(description = "수정할 장비 정보 목록") List<@Valid EquipmentUpdateInfo> outsourcingEquipments) {

    @Schema(description = "장비 정보 수정 내용")
    public record EquipmentUpdateInfo(
            @Schema(description = "ID", example = "1") Long id,
            @Schema(description = "외주업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
            @Schema(description = "외주업체계약 기사 ID", example = "1") @NotNull Long outsourcingCompanyContractDriverId,
            @Schema(description = "외주업체계약 장비 ID", example = "1") @NotNull Long outsourcingCompanyContractEquipmentId,
            @Schema(description = "작업내용", example = "기초 굴착 작업") @NotBlank String workContent,
            @Schema(description = "단가", example = "50000") @NotNull Long unitPrice,
            @Schema(description = "작업시간", example = "1.0") @NotNull Double workHours,
            @Schema(description = "비고", example = "정상 작동") String memo) {
    }
}

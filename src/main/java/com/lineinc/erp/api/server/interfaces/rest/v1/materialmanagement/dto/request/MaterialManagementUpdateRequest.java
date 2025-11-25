package com.lineinc.erp.api.server.interfaces.rest.v1.materialmanagement.dto.request;

import java.time.LocalDate;
import java.util.List;
import com.lineinc.erp.api.server.domain.materialmanagement.enums.MaterialManagementInputType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "자재관리 수정 요청")
public record MaterialManagementUpdateRequest(
        @Schema(description = "현장 ID", example = "1") @NotNull Long siteId,
        @Schema(description = "공정 ID", example = "1") @NotNull Long siteProcessId,
        @Schema(description = "외주업체 ID", example = "1") @NotNull Long outsourcingCompanyId,
        @Schema(description = "공제업체 ID", example = "1") Long deductionCompanyId,
        @Schema(description = "공제업체 계약 ID", example = "1") Long deductionCompanyContractId,
        @Schema(description = "투입 구분",
                example = "MAJOR_PURCHASE") @NotNull MaterialManagementInputType inputType,
        @Schema(description = "투입 구분 상세", example = "외주사 납품") String inputTypeDescription,
        @Schema(description = "요청일자", example = "2024-07-28") @NotNull LocalDate deliveryDate,
        @Schema(description = "비고", example = "1차 자재 납품 완료") String memo,
        @Schema(description = "자재 상세 목록") @Size(
                min = 1) @Valid List<MaterialManagementDetailUpdateRequest> details,
        @Schema(description = "자재 파일 목록") @Valid List<MaterialManagementFileUpdateRequest> files,
        @Schema(description = "수정 이력 리스트") @Valid List<MaterialManagementUpdateRequest.ChangeHistoryRequest> changeHistories) {
    public record ChangeHistoryRequest(@Schema(description = "수정 이력 번호", example = "1") Long id,
            @Schema(description = "변경 사유 또는 비고", example = "변경에 따른 업데이트") String memo) {
    }
}

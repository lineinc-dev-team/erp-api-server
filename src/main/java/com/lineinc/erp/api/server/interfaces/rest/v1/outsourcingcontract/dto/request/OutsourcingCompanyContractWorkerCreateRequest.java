package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "외주업체 계약 인력 등록 요청")
public record OutsourcingCompanyContractWorkerCreateRequest(
        @Schema(description = "인력명", example = "김철수") @NotBlank String name,

        @Schema(description = "인력 구분", example = "기술자") @NotBlank String category,

        @Schema(description = "작업내용", example = "설비 설치 및 조정") @NotBlank String taskDescription,

        @Schema(description = "비고", example = "경력 10년 이상") String memo,

        @Schema(description = "인력 서류 목록") @Valid List<OutsourcingCompanyContractWorkerFileCreateRequest> files) {
}

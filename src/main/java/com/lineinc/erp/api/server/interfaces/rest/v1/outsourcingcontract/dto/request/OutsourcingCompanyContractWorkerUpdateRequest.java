package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 인력 수정 요청")
public record OutsourcingCompanyContractWorkerUpdateRequest(
        @Schema(description = "인력 ID", example = "1") Long id,

        @Schema(description = "인력명", example = "김철수") String name,

        @Schema(description = "인력 구분", example = "기술자") String category,

        @Schema(description = "작업내용", example = "설비 설치 및 조정") String taskDescription,

        @Schema(description = "비고", example = "경력 10년 이상") String memo,

        @Schema(description = "인력 서류 목록") List<OutsourcingCompanyContractWorkerFileUpdateRequest> files) {
}

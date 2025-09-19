package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractWorker;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 인력 정보 응답")
public record ContractWorkerResponse(
        @Schema(description = "인력 ID", example = "1") Long id,
        @Schema(description = "이름", example = "김철수") String name,
        @Schema(description = "카테고리", example = "용접공") String category,
        @Schema(description = "작업내용", example = "강재 용접 작업") String taskDescription,
        @Schema(description = "메모", example = "경험 많은 용접공") String memo,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt,
        @Schema(description = "인력 파일 목록") List<ContractWorkerFileResponse> files) {

    public static ContractWorkerResponse from(final OutsourcingCompanyContractWorker worker) {
        return new ContractWorkerResponse(
                worker.getId(),
                worker.getName(),
                worker.getCategory(),
                worker.getTaskDescription(),
                worker.getMemo(),
                worker.getCreatedAt(),
                worker.getUpdatedAt(),
                worker.getFiles() != null ? worker.getFiles().stream()
                        .map(ContractWorkerFileResponse::from)
                        .toList() : List.of());
    }

    @Schema(description = "외주업체 계약 인력 간단 정보 응답")
    public record ContractWorkerSimpleResponse(
            @Schema(description = "인력 ID", example = "1") Long id,
            @Schema(description = "인력명", example = "김철수") String name,
            @Schema(description = "카테고리", example = "용접공") String category,
            @Schema(description = "삭제 여부", example = "false") Boolean deleted) {

        public static ContractWorkerSimpleResponse from(final OutsourcingCompanyContractWorker worker) {
            return new ContractWorkerSimpleResponse(
                    worker.getId(),
                    worker.getName(),
                    worker.getCategory(),
                    worker.isDeleted());
        }
    }
}

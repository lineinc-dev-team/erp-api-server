package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractDriver;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 기사(운전자) 정보 응답")
public record ContractDriverResponse(
        @Schema(description = "기사 ID", example = "1") Long id,
        @Schema(description = "기사명", example = "김운전") String name,
        @Schema(description = "메모", example = "경험 많은 기사") String memo,
        @Schema(description = "생성일시") OffsetDateTime createdAt,
        @Schema(description = "수정일시") OffsetDateTime updatedAt,
        @Schema(description = "기사 서류 목록") List<ContractDriverFileResponse> files) {

    public static ContractDriverResponse from(final OutsourcingCompanyContractDriver driver) {
        return new ContractDriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getMemo(),
                driver.getCreatedAt(),
                driver.getUpdatedAt(),
                driver.getFiles() != null ? driver.getFiles().stream()
                        .map(ContractDriverFileResponse::from)
                        .toList() : List.of());
    }

    @Schema(description = "외주업체 계약 기사(운전자) 간단 정보 응답")
    public record ContractDriverSimpleResponse(
            @Schema(description = "기사 ID", example = "1") Long id,
            @Schema(description = "기사명", example = "김운전") String name,
            @Schema(description = "삭제 여부", example = "false") Boolean deleted) {

        public static ContractDriverSimpleResponse from(final OutsourcingCompanyContractDriver driver) {
            return new ContractDriverSimpleResponse(
                    driver.getId(),
                    driver.getName(),
                    driver.isDeleted());
        }
    }
}

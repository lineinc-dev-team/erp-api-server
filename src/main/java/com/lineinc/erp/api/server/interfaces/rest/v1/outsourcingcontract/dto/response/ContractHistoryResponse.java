package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.lineinc.erp.api.server.domain.outsourcingcontract.entity.OutsourcingCompanyContractHistory;
import com.lineinc.erp.api.server.domain.outsourcingcontract.enums.OutsourcingCompanyContractDefaultDeductionsType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 이력 응답")
public record ContractHistoryResponse(
        @Schema(description = "계약 ID") Long contractId,

        @Schema(description = "현장명") String siteName,

        @Schema(description = "공정명") String processName,

        @Schema(description = "외주금액") Long contractAmount,

        @Schema(description = "외주업체 계약 구분값") String type,

        @Schema(description = "담당자명") String contactName,

        @Schema(description = "공제항목") String defaultDeductions,

        @Schema(description = "첨부파일 목록") List<ContractFileResponse> files,

        @Schema(description = "계약 시작일") OffsetDateTime contractStartDate,

        @Schema(description = "계약 종료일") OffsetDateTime contractEndDate,

        @Schema(description = "등록일") OffsetDateTime createdAt,

        @Schema(description = "수정일") OffsetDateTime updatedAt) {

    public static ContractHistoryResponse from(OutsourcingCompanyContractHistory history) {
        var contract = history.getContract();

        // 담당자명 (대표 담당자 우선, 없으면 첫 번째 담당자)
        String contactName = contract.getContacts() != null && !contract.getContacts().isEmpty()
                ? contract.getContacts().stream()
                        .filter(contact -> contact.getIsMain())
                        .findFirst()
                        .orElse(contract.getContacts().get(0))
                        .getName()
                : null;

        // 첨부파일 목록
        List<ContractFileResponse> files = contract.getFiles() != null
                ? contract.getFiles().stream()
                        .map(ContractFileResponse::from)
                        .toList()
                : new ArrayList<>();

        // 공제항목을 콤마로 구분하여 각각의 label로 변환
        String defaultDeductionsLabel = null;
        if (contract.getDefaultDeductions() != null && !contract.getDefaultDeductions().trim().isEmpty()) {
            defaultDeductionsLabel = java.util.Arrays.stream(contract.getDefaultDeductions().split(","))
                    .map(String::trim)
                    .map(OutsourcingCompanyContractDefaultDeductionsType::safeLabelOf)
                    .collect(java.util.stream.Collectors.joining(","));
        }

        return new ContractHistoryResponse(
                history.getId(),
                contract.getSite() != null ? contract.getSite().getName() : null,
                contract.getSiteProcess() != null ? contract.getSiteProcess().getName() : null,
                contract.getContractAmount(),
                history.getContract().getType().getLabel(),
                contactName,
                defaultDeductionsLabel,
                files,
                contract.getContractStartDate(),
                contract.getContractEndDate(),
                history.getCreatedAt(),
                history.getUpdatedAt());
    }
}

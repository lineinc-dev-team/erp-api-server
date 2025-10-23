package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcingcontract.dto.response;

import java.util.List;

import com.lineinc.erp.api.server.domain.outsourcingcompanycontract.entity.OutsourcingCompanyContractConstructionGroup;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외주업체 계약 공사항목 그룹 정보 응답")
public record ContractConstructionGroupResponse(
        @Schema(description = "공사항목 그룹 ID", example = "1") Long id,
        @Schema(description = "항목명", example = "토공사") String itemName,
        @Schema(description = "삭제 여부", example = "false") Boolean deleted,
        @Schema(description = "공사항목 목록") List<ContractConstructionResponse.ContractConstructionSimpleResponse> constructions) {

    public static ContractConstructionGroupResponse from(final OutsourcingCompanyContractConstructionGroup group) {
        return new ContractConstructionGroupResponse(
                group.getId(),
                group.getItemName(),
                group.isDeleted(),
                group.getConstructions() != null ? group.getConstructions().stream()
                        .map(ContractConstructionResponse.ContractConstructionSimpleResponse::from)
                        .toList() : List.of());
    }

}
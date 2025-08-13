package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "외주업체 계약 삭제 요청")
public record DeleteOutsourcingCompanyContractsRequest(
        @NotEmpty @Schema(description = "삭제할 외주업체 계약 ID 목록", example = "[1, 2, 3]") List<Long> contractIds) {
}

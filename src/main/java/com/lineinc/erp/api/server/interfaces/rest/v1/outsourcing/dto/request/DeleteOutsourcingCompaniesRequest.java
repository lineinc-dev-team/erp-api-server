package com.lineinc.erp.api.server.interfaces.rest.v1.outsourcing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "외주업체 삭제 요청")
public record DeleteOutsourcingCompaniesRequest(
        @NotEmpty
        @Schema(description = "삭제할 외주업체 ID 목록", example = "[1, 2, 3]")
        List<Long> outsourcingCompanyIds
) {
}

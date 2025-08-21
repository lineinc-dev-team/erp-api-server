package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "인력정보 삭제 요청")
public record DeleteLaborsRequest(
        @NotEmpty @Schema(description = "삭제할 인력정보 ID 목록", example = "[1, 2, 3]") List<Long> laborIds) {
}

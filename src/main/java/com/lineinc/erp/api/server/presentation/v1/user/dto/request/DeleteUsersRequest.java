package com.lineinc.erp.api.server.presentation.v1.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "유저 삭제 요청")
public record DeleteUsersRequest(
        @NotEmpty
        @Schema(description = "삭제할 유저 ID 목록", example = "[1, 2, 3]")
        List<Long> userIds
) {
}

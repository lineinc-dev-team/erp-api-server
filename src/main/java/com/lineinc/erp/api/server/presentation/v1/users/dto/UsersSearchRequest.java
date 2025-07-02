package com.lineinc.erp.api.server.presentation.v1.users.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Schema(description = "사용자 검색 및 페이징 요청")
public record UsersSearchRequest(
        @Min(value = 0, message = "page는 0 이상이어야 합니다")
        @Schema(description = "0부터 시작하는 페이지 번호", example = "0")
        int page,

        @Min(value = 1, message = "size는 1 이상이어야 합니다")
        @Max(value = 200, message = "size는 최대 200까지 가능합니다")
        @Schema(description = "한 페이지에 포함될 아이템 수", example = "20")
        int size,

        @Schema(description = "정렬 조건", example = "loginId,asc")
        String sort
) {
}
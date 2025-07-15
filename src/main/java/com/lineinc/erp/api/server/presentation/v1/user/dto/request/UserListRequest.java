package com.lineinc.erp.api.server.presentation.v1.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@ParameterObject
@Schema(description = "사용자 검색 및 페이징 요청")
public record UserListRequest(

        @Schema(description = "사용자 이름", example = "홍길동")
        String username,

        @Schema(description = "권한 그룹 ID", example = "1")
        Long roleId,

        @Schema(description = "계정 상태 (true: 활성, false: 비활성)", example = "true")
        Boolean isActive,

        @Schema(description = "생성일(시작일)", example = "2024-01-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createdStartDate,

        @Schema(description = "생성일(종료일)", example = "2024-12-31")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createdEndDate,

        @Schema(description = "최종 로그인일(시작일)", example = "2024-01-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate lastLoginStartDate,

        @Schema(description = "최종 로그인일(종료일)", example = "2024-12-31")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate lastLoginEndDate

) {
}
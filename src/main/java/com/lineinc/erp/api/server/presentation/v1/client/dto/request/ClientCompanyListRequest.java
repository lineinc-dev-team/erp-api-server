package com.lineinc.erp.api.server.presentation.v1.client.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ParameterObject
@Schema(description = "발주처 검색 요청")
public record ClientCompanyListRequest(
        @Schema(description = "발주처명", example = "삼성")
        String name,

        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,

        @Schema(description = "대표자명", example = "홍길동")
        String ceoName,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "유선 전화번호", example = "02-124-5678")
        String landlineNumber,

        @Schema(description = "발주처 담당자명", example = "홍길동")
        String contactName,

        @Schema(description = "이메일", example = "example@samsung.com")
        String email,

        @Schema(description = "본사 담당자명", example = "김영희")
        String userName,

        @Schema(description = "생성일(시작일)", example = "2024-01-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createdStartDate,

        @Schema(description = "생성일(종료일)", example = "2025-12-31")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createdEndDate,

        @Schema(description = "사용 여부", example = "true")
        Boolean isActive
) {
}
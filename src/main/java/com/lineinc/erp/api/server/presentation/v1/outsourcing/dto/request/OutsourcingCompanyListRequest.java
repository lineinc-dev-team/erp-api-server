package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "외주업체 목록 조회 요청")
public record OutsourcingCompanyListRequest(

        @Schema(description = "업체명", example = "삼성ENG")
        String name,

        @Schema(description = "사업자등록번호", example = "123-45-67890")
        String businessNumber,

        @Schema(description = "대표자명", example = "김대표")
        String ceoName,

        @Schema(description = "전화번호", example = "02-124-5678")
        String landlineNumber,

        @Schema(description = "외주업체 구분", example = "EQUIPMENT")
        OutsourcingCompanyType type,

        @Schema(description = "생성일(시작일)", example = "2024-01-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createdStartDate,

        @Schema(description = "생성일(종료일)", example = "2025-12-31")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createdEndDate,

        @Schema(description = "사용여부", example = "true")
        Boolean isActive

) {
}

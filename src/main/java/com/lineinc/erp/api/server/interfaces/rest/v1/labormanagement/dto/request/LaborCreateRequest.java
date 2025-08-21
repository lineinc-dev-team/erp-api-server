package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.WorkType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "노무 생성 요청")
public record LaborCreateRequest(
        @Schema(description = "노무 구분", example = "OUTSOURCING") LaborType type,

        @Schema(description = "구분 설명", example = "현장 작업용역") String typeDescription,

        @Schema(description = "소속업체 ID", example = "1") Long outsourcingCompanyId,

        @Schema(description = "이름", example = "홍길동") String name,

        @Schema(description = "주민등록번호", example = "123456-1234567") String residentNumber,

        @Schema(description = "주소", example = "서울시 강남구") String address,

        @Schema(description = "상세주소", example = "테헤란로 123") String detailAddress,

        @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,

        @Schema(description = "비고", example = "추가 메모") String memo,

        @Schema(description = "공종", example = "WELDER") WorkType workType,

        @Schema(description = "공종 설명", example = "용접 작업") String workTypeDescription,

        @Schema(description = "주작업", example = "강재 용접") String mainWork,

        @Schema(description = "기준일당", example = "150000") Long dailyWage,

        @Schema(description = "은행명", example = "신한은행") String bankName,

        @Schema(description = "계좌번호", example = "110-123456789") String accountNumber,

        @Schema(description = "예금주", example = "홍길동") String accountHolder,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "입사일", example = "2024-01-01") LocalDate hireDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Schema(description = "퇴사일", example = "2024-12-31") LocalDate resignationDate,

        @Schema(description = "첨부파일 목록") List<LaborFileCreateRequest> files

) {
}

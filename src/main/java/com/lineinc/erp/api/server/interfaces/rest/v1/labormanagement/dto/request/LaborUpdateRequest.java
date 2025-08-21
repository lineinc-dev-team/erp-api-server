package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.WorkType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인력정보 수정 요청")
public record LaborUpdateRequest(
        @Schema(description = "노무 구분", example = "ETC") LaborType type,
        @Schema(description = "구분 설명", example = "기술공") String typeDescription,
        @Schema(description = "이름", example = "홍길동") String name,
        @Schema(description = "주민등록번호", example = "860101-1234567") String residentNumber,
        @Schema(description = "공종", example = "SCAFFOLDING") WorkType workType,
        @Schema(description = "공종 설명", example = "콘크리트 공사") String workTypeDescription,
        @Schema(description = "주작업", example = "콘크리트 타설") String mainWork,
        @Schema(description = "기준일당", example = "150000") Long dailyWage,
        @Schema(description = "은행명", example = "신한은행") String bankName,
        @Schema(description = "계좌번호", example = "123-456-789012") String accountNumber,
        @Schema(description = "예금주", example = "홍길동") String accountHolder,
        @Schema(description = "입사일", example = "2024-01-01") LocalDate hireDate,
        @Schema(description = "퇴사일", example = "2024-12-31") LocalDate resignationDate,
        @Schema(description = "외주업체 ID", example = "1") Long outsourcingCompanyId,
        @Schema(description = "주소", example = "서울시 강남구") String address,
        @Schema(description = "상세주소", example = "역삼동 123-45") String detailAddress,
        @Schema(description = "휴대폰", example = "010-1234-5678") String phoneNumber,
        @Schema(description = "비고", example = "경력 10년") String memo,
        @Schema(description = "첨부파일 목록") List<LaborFileUpdateRequest> files) {
}

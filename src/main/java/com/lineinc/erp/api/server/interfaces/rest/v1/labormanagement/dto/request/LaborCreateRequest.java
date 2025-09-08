package com.lineinc.erp.api.server.interfaces.rest.v1.labormanagement.dto.request;

import java.util.List;

import com.lineinc.erp.api.server.domain.labormanagement.enums.LaborType;
import com.lineinc.erp.api.server.domain.labormanagement.enums.WorkType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "노무 생성 요청")
public record LaborCreateRequest(
        @NotNull @Schema(description = "노무 구분", example = "DIRECT_CONTRACT") LaborType type,
        @Schema(description = "구분 설명", example = "현장 작업용역") String typeDescription,
        @NotNull @Schema(description = "소속업체 ID", example = "1") Long outsourcingCompanyId,
        @NotBlank @Schema(description = "이름", example = "홍길동") String name,
        @NotBlank @Schema(description = "주민등록번호", example = "123456-1234567") String residentNumber,
        @NotBlank @Schema(description = "주소", example = "서울시 강남구") String address,
        @NotBlank @Schema(description = "상세주소", example = "테헤란로 123") String detailAddress,
        @NotBlank @Schema(description = "개인 휴대폰", example = "010-1234-5678") String phoneNumber,
        @Schema(description = "비고", example = "추가 메모") String memo,
        @Schema(description = "공종", example = "WELDER") WorkType workType,
        @Schema(description = "공종 설명", example = "용접 작업") String workTypeDescription,
        @NotBlank @Schema(description = "주작업", example = "강재 용접") String mainWork,
        @NotNull @Schema(description = "기준일당", example = "150000") Long dailyWage,
        @NotBlank @Schema(description = "은행명", example = "신한은행") String bankName,
        @NotBlank @Schema(description = "계좌번호", example = "110-123456789") String accountNumber,
        @NotBlank @Schema(description = "예금주", example = "홍길동") String accountHolder,
        @Schema(description = "첨부파일 목록") @Valid List<LaborFileCreateRequest> files) {
}

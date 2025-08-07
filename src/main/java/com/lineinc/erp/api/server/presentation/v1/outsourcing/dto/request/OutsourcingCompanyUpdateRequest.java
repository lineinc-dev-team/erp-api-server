package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.request;

import java.util.List;

import com.lineinc.erp.api.server.domain.outsourcing.enums.OutsourcingCompanyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "외주업체 수정 요청")
public record OutsourcingCompanyUpdateRequest(
        @Schema(description = "외주업체명", example = "삼성ENG")
        String name,

        @Schema(description = "구분", example = "SERVICE")
        OutsourcingCompanyType type,

        @Schema(description = "구분 설명", example = "직접 시공 업체")
        String typeDescription,

        @Schema(description = "대표자명", example = "김대표")
        String ceoName,

        @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
        String address,

        @Schema(description = "상세주소", example = "5층 501호")
        String detailAddress,

        @Schema(description = "전화번호", example = "02-123-4567")
        String landlineNumber,

        @Schema(description = "휴대폰번호", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "이메일", example = "ceo@outsourcing.com")
        @Email
        String email,

        @Schema(description = "활성 여부", example = "true")
        Boolean isActive,

        @Schema(description = "기본 공제 항목 (콤마로 구분된 문자열)", example = "FUEL_COST,MEAL_COST")
        String defaultDeductions,

        @Schema(description = "기본 공제 항목 설명", example = "3.3% 원천징수")
        String defaultDeductionsDescription,

        @Schema(description = "은행명", example = "국민은행")
        String bankName,

        @Schema(description = "계좌번호", example = "123456-78-901234")
        String accountNumber,

        @Schema(description = "예금주", example = "홍길순")
        String accountHolder,

        @Schema(description = "비고")
        String memo,

        @Schema(description = "담당자 목록")
        List<OutsourcingCompanyContactUpdateRequest> contacts,

        @Schema(description = "첨부파일 목록")
        List<OutsourcingCompanyFileUpdateRequest> files,

        @Schema(description = "수정 이력 리스트")
        List<OutsourcingCompanyUpdateRequest.ChangeHistoryRequest> changeHistories
) {
    public record ChangeHistoryRequest(
            @Schema(description = "수정 이력 번호", example = "1")
            Long id,

            @Schema(description = "변경 사유 또는 비고", example = "변경에 따른 업데이트")
            String memo
    ) {
    }
}

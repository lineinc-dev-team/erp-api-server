package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContact;

@Schema(description = "외주업체 담당자 응답")
public record CompanyContactResponse(

        @Schema(description = "ID", example = "101")
        Long id,

        @Schema(description = "담당자명", example = "홍길동")
        String name,

        @Schema(description = "부서", example = "영업팀")
        String department,

        @Schema(description = "직급", example = "과장")
        String position,

        @Schema(description = "전화번호", example = "02-1234-5678")
        String landlineNumber,

        @Schema(description = "개인 휴대폰", example = "010-9876-5432")
        String phoneNumber,

        @Schema(description = "이메일", example = "honggildong@example.com")
        String email,

        @Schema(description = "비고", example = "주요 거래처 담당")
        String memo,

        @Schema(description = "대표 담당자 여부", example = "true")
        Boolean isMain

) {
    public static CompanyContactResponse from(OutsourcingCompanyContact contact) {
        return new CompanyContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getDepartment(),
                contact.getPosition(),
                contact.getLandlineNumber(),
                contact.getPhoneNumber(),
                contact.getEmail(),
                contact.getMemo(),
                contact.getIsMain()
        );
    }
}

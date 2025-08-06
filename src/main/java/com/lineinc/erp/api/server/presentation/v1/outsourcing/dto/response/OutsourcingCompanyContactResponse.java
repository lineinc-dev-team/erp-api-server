package com.lineinc.erp.api.server.presentation.v1.outsourcing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.lineinc.erp.api.server.domain.outsourcing.entity.OutsourcingCompanyContact;

@Schema(description = "외주업체 담당자 응답")
public record OutsourcingCompanyContactResponse(

        @Schema(description = "ID")
        Long id,

        @Schema(description = "담당자명")
        String name,

        @Schema(description = "부서")
        String department,

        @Schema(description = "직급")
        String position,

        @Schema(description = "전화번호")
        String landlineNumber,

        @Schema(description = "개인 휴대폰")
        String phoneNumber,

        @Schema(description = "이메일")
        String email,

        @Schema(description = "비고")
        String memo,

        @Schema(description = "대표 담당자 여부")
        Boolean isMain

) {
    public static OutsourcingCompanyContactResponse from(OutsourcingCompanyContact contact) {
        return new OutsourcingCompanyContactResponse(
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

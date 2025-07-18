package com.lineinc.erp.api.server.presentation.v1.client.dto.response;

import com.lineinc.erp.api.server.domain.client.entity.ClientCompanyContact;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "발주처 담당자 정보 응답")
public record ClientCompanyContactResponse(

        @Schema(description = "발주처 담당자 ID", example = "123")
        Long id,

        @Schema(description = "담당자명", example = "김철수")
        String name,

        @Schema(description = "연락처", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "유선전화", example = "02-9876-5432")
        String landlineNumber,

        @Schema(description = "이메일", example = "kim@example.com")
        String email,

        @Schema(description = "직급 / 부서", example = "팀장")
        String position,

        @Schema(description = "비고 / 메모")
        String memo,

        @Schema(description = "등록일", example = "2024-01-01T12:34:56")
        String createdAt,

        @Schema(description = "수정일", example = "2024-01-01T12:34:56")
        String updatedAt
) {
    public static ClientCompanyContactResponse from(ClientCompanyContact contact) {
        return new ClientCompanyContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getPhoneNumber(),
                contact.getLandlineNumber(),
                contact.getEmail(),
                contact.getPosition(),
                contact.getMemo(),
                contact.getCreatedAt().toString(),
                contact.getUpdatedAt().toString()
        );
    }
}
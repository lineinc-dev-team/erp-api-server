package com.lineinc.erp.api.server.presentation.v1.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@ParameterObject
@Schema(description = "유저 엑셀 다운로드 요청")
public record UserDownloadRequest(
        @NotBlank
        @Schema(
                description = "허용 필드: id, loginId, username, roleName, isActive, lastLoginAt, createdAt, updatedAt, updatedBy, memo",
                example = "id,username,isActive"
        )
        String fields
) {
    public static final List<String> ALLOWED_FIELDS = List.of(
            "id", "loginId", "username", "roleName", "isActive",
            "lastLoginAt", "createdAt", "updatedAt", "updatedBy", "memo"
    );
}
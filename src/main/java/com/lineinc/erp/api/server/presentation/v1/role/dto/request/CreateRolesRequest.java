package com.lineinc.erp.api.server.presentation.v1.role.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "권한 그룹 생성 요청")
public record CreateRolesRequest(
        @NotBlank
        @Schema(description = "권한 그룹 이름", example = "현장 관리자")
        String name,

        @Schema(description = "메모", example = "특별 권한 그룹입니다.")
        String memo,

        @Schema(description = "계정 및 메모 리스트", example = "[{\"userId\": 1, \"memo\": \"현장 책임자\"}]")
        List<UserWithMemo> users,

        @Schema(description = "권한 ID 리스트", example = "[1, 2, 3]")
        List<Long> permissionIds,

        @Schema(description = "전체 현장 및 공정 접근 권한 여부", example = "true")
        Boolean hasGlobalSiteProcessAccess,

        @Schema(description = "현장 및 공정 접근 리스트", example = "[{\"siteId\": 1, \"processId\": 3}]")
        List<SiteProcessAccess> siteProcesses
) {

    public static record UserWithMemo(
            @Schema(description = "계정 ID", example = "1")
            Long userId,

            @Schema(description = "비고 / 메모", example = "현장 책임자")
            String memo
    ) {
    }

    public static record SiteProcessAccess(
            @Schema(description = "현장 ID", example = "1")
            Long siteId,

            @Schema(description = "공정 ID", example = "3")
            Long processId
    ) {
    }
}
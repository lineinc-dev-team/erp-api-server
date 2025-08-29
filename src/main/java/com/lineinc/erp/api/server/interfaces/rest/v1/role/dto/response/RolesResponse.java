package com.lineinc.erp.api.server.interfaces.rest.v1.role.dto.response;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.entity.RoleSiteProcess;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteProcessResponse;
import com.lineinc.erp.api.server.interfaces.rest.v1.site.dto.response.SiteResponse;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "권한 그룹 리스트 응답")
public record RolesResponse(
        @Schema(description = "권한 그룹 ID", example = "1") Long id,

        @Schema(description = "권한 그룹 이름", example = "전체권한") String name,

        @Schema(description = "해당 권한 그룹에 속한 유저 수", example = "5") int userCount,

        @Schema(description = "생성일시", example = "2024-06-01T10:00:00") OffsetDateTime createdAt,

        @Schema(description = "수정일시", example = "2024-07-01T12:00:00") OffsetDateTime updatedAt,

        @Schema(description = "메모", example = "특별 권한 그룹입니다.") String memo,

        @Schema(description = "전체 현장-공정 접근 권한 여부", example = "false") boolean hasGlobalSiteProcessAccess,

        @Schema(description = "현장 정보 목록") List<SiteResponse.SiteSimpleResponse> sites,

        @Schema(description = "공정 정보 목록") List<SiteProcessResponse.SiteProcessSimpleResponse> processes) {
    public static RolesResponse from(Role role) {
        int userCount = role.getUserRoles() == null ? 0
                : (int) role.getUserRoles().stream()
                        .map(UserRole::getUser)
                        .distinct()
                        .count();

        List<SiteResponse.SiteSimpleResponse> sites = role.getSiteProcesses() == null ? List.of()
                : role.getSiteProcesses().stream()
                        .map(RoleSiteProcess::getSite)
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .map(SiteResponse.SiteSimpleResponse::from)
                        .toList();

        List<SiteProcessResponse.SiteProcessSimpleResponse> processes = role.getSiteProcesses() == null ? List.of()
                : role.getSiteProcesses().stream()
                        .map(RoleSiteProcess::getProcess)
                        .filter(java.util.Objects::nonNull)
                        .distinct()
                        .map(SiteProcessResponse.SiteProcessSimpleResponse::from)
                        .toList();

        return new RolesResponse(
                role.getId(),
                role.getName(),
                userCount,
                role.getCreatedAt(),
                role.getUpdatedAt(),
                role.getMemo(),
                role.isHasGlobalSiteProcessAccess(),
                sites,
                processes);
    }

}

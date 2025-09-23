package com.lineinc.erp.api.server.interfaces.rest.v1.menu.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메뉴 및 해당 메뉴에 속한 권한 목록 응답")
public record MenuWithPermissionsResponse(
        @Schema(description = "메뉴 ID", example = "1") Long id,
        @Schema(description = "메뉴명", example = "계정관리") String name,
        @Schema(description = "메뉴 순서", example = "1") Integer order,
        @Schema(description = "해당 메뉴에 할당된 권한 리스트") List<PermissionResponse> permissions) {
    @Schema(description = "권한 정보")
    public record PermissionResponse(
            @Schema(description = "권한 ID", example = "10") Long id,
            @Schema(description = "권한 액션명", example = "조회") String action) {
    }

    public static MenuWithPermissionsResponse from(final Menu menu) {
        final List<PermissionResponse> permissionResponses = menu.getPermissions().stream()
                .map(permission -> new PermissionResponse(permission.getId(), permission.getAction().getLabel()))
                .collect(Collectors.toList());
        return new MenuWithPermissionsResponse(menu.getId(), menu.getName(), menu.getOrder(), permissionResponses);
    }
}
package com.lineinc.erp.api.server.presentation.v1.role.dto.response;

import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Comparator;

@Schema(description = "권한 그룹 메뉴별 권한 응답")
public record MenusPermissionsResponse(
        @Schema(description = "메뉴 ID", example = "1")
        Long id,
        @Schema(description = "메뉴 이름", example = "전체권한")
        String name,

        @Schema(description = "권한")
        List<PermissionDto> permissions
) {
    public static MenusPermissionsResponse from(Long menuId, String menuName, List<Permission> permissions) {
        List<PermissionDto> permissionItems = permissions.stream()
                .map(p -> new PermissionDto(p.getId(), p.getAction().getLabel()))
                .sorted(Comparator.comparing(PermissionDto::id))
                .toList();

        return new MenusPermissionsResponse(menuId, menuName, permissionItems);
    }

    public record PermissionDto(
            @Schema(description = "권한 ID", example = "1")
            Long id,
            @Schema(description = "권한 이름", example = "조회")
            String action
    ) {
    }
}

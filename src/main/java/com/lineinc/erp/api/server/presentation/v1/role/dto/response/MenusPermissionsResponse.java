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
        List<Permission> permissions
) {
    public static MenusPermissionsResponse from(Long menuId, String menuName, List<com.lineinc.erp.api.server.domain.permission.entity.Permission> permissions) {
        List<Permission> permissionItems = permissions.stream()
                .map(p -> new Permission(p.getId(), p.getAction().getLabel()))
                .sorted(Comparator.comparing(Permission::id))
                .toList();

        return new MenusPermissionsResponse(menuId, menuName, permissionItems);
    }

    public record Permission(
            @Schema(description = "권한 ID", example = "1")
            Long id,
            @Schema(description = "권한 이름", example = "조회")
            String action
    ) {

    }
}

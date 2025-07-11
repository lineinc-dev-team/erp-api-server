package com.lineinc.erp.api.server.presentation.v1.role.dto.response;

import com.lineinc.erp.api.server.presentation.v1.menu.dto.response.MenuWithPermissionsResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.role.entity.Role;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

@Schema(description = "권한 그룹과 연결된 메뉴 및 권한 정보 응답")
public record RoleWithMenusResponse(

        @Schema(description = "권한 그룹 ID", example = "1")
        Long id,

        @Schema(description = "권한 그룹 이름", example = "전체권한")
        String roleName,

        @Schema(description = "권한 그룹에 속한 메뉴 및 권한 목록")
        List<MenuWithPermissionsResponse> menus
) {
    public static RoleWithMenusResponse from(Role role) {
        // Role의 권한들을 메뉴 기준으로 그룹핑 (Map<Menu, List<Permission>>)
        Map<Menu, List<Permission>> menuToPermissions = role.getPermissions().stream()
                .collect(Collectors.groupingBy(Permission::getMenu));

        List<MenuWithPermissionsResponse> menus = new ArrayList<>();
        // 메뉴별 권한 리스트를 DTO로 변환하여 메뉴 DTO 리스트 생성
        for (Map.Entry<Menu, List<Permission>> entry : menuToPermissions.entrySet()) {
            Menu menu = entry.getKey();
            List<MenuWithPermissionsResponse.PermissionResponse> permissions = entry.getValue().stream()
                    .sorted(Comparator.comparing(Permission::getId))
                    .map(p -> new MenuWithPermissionsResponse.PermissionResponse(p.getId(), p.getAction().getLabel()))
                    .toList();

            menus.add(new MenuWithPermissionsResponse(menu.getId(), menu.getName(), permissions));
        }

        // Role ID, 이름, 메뉴별 권한 DTO 리스트를 포함하는 최종 DTO 반환
        return new RoleWithMenusResponse(role.getId(), role.getName(), menus);
    }
}
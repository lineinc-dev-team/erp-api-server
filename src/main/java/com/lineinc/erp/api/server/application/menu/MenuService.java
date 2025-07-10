package com.lineinc.erp.api.server.application.menu;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.menu.repository.MenuRepository;
import com.lineinc.erp.api.server.domain.permission.repository.PermissionRepository;
import com.lineinc.erp.api.server.presentation.v1.menu.dto.response.MenuWithPermissionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    /**
     * 모든 메뉴와 메뉴별 권한을 포함하여 반환
     */
    public List<MenuWithPermissionsResponse> getMenusWithPermissions() {
        List<Menu> menus = menuRepository.findAllWithPermissions();

        return menus.stream()
                .map(menu -> {
                    List<MenuWithPermissionsResponse.PermissionResponse> permissions = menu.getPermissions().stream()
                            .map(permission -> new MenuWithPermissionsResponse.PermissionResponse(permission.getId(), permission.getAction().getLabel()))
                            .collect(Collectors.toList());

                    return new MenuWithPermissionsResponse(menu.getId(), menu.getName(), permissions);
                })
                .collect(Collectors.toList());
    }

}
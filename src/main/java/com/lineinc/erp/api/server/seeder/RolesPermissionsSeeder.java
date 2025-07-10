package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.menu.repository.MenuRepository;
import com.lineinc.erp.api.server.domain.permission.repository.PermissionRepository;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;

/**
 * 역할(Role)과 권한(Permission)을 매핑하는 시더
 * 예: 계정관리 매니저 역할은 계정관리 메뉴에 대해 삭제를 제외한 권한을 가진다.
 */
@Component
@RequiredArgsConstructor
public class RolesPermissionsSeeder {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public void seed() {
        // 계정관리 매니저 역할 → 계정관리 메뉴에 대한 VIEW, CREATE, UPDATE, APPROVE 권한 할당 (DELETE 제외)
        Optional<Role> roleOpt = roleRepository.findByName(AppConstants.ROLE_ACCOUNT_MANAGER_NAME);
        Optional<Menu> menuOpt = menuRepository.findByName(AppConstants.MENU_ACCOUNT);

        if (roleOpt.isEmpty() || menuOpt.isEmpty()) return;

        Role role = roleOpt.get();
        Menu menu = menuOpt.get();

        // DELETE 권한을 제외한 모든 PermissionAction 값을 포함한 EnumSet 생성
        // → VIEW, CREATE, UPDATE, APPROVE 만 포함됨
        EnumSet<PermissionAction> allowedActions = EnumSet.complementOf(EnumSet.of(PermissionAction.DELETE));

        for (PermissionAction action : allowedActions) {
            permissionRepository.findByMenuAndAction(menu, action).ifPresent(permission -> {
                role.getPermissions().add(permission);
            });
        }

        roleRepository.save(role);
    }
}
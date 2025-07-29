package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.menu.repository.MenuRepository;
import com.lineinc.erp.api.server.domain.permission.repository.PermissionRepository;
import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.entity.RolePermission;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

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
        assignAllPermissionsToRole();
        assignPermissionsToRole(AppConstants.ROLE_SUB_ADMIN_NAME, false);
        assignPermissionsToRole(AppConstants.ROLE_SUB_ADMIN_WITHOUT_PERMISSION_MENU, true);
    }

    private void assignPermissionsToRole(String roleName, boolean excludeRoleMenu) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));

        EnumSet<PermissionAction> actions = EnumSet.complementOf(EnumSet.of(PermissionAction.DELETE));

        for (Menu m : menuRepository.findAll()) {
            if (excludeRoleMenu && AppConstants.MENU_PERMISSION.equals(m.getName())) continue;

            for (PermissionAction action : actions) {
                permissionRepository.findByMenuAndAction(m, action).ifPresent(permission -> {
                    RolePermission rolePermission = RolePermission.builder()
                            .role(role)
                            .permission(permission)
                            .menu(m)
                            .build();
                    role.getPermissions().add(rolePermission);
                });
            }
        }

        roleRepository.save(role);
    }

    private void assignAllPermissionsToRole() {
        Role role = roleRepository.findByName(AppConstants.ROLE_ADMIN_NAME)
                .orElseGet(() -> roleRepository.save(Role.builder().name(AppConstants.ROLE_ADMIN_NAME).build()));

        menuRepository.findAll().forEach(menu ->
                EnumSet.allOf(PermissionAction.class).forEach(action ->
                        permissionRepository.findByMenuAndAction(menu, action).ifPresent(permission -> {
                            RolePermission rolePermission = RolePermission.builder()
                                    .role(role)
                                    .permission(permission)
                                    .menu(menu)
                                    .build();
                            role.getPermissions().add(rolePermission);
                        })
                )
        );

        roleRepository.save(role);
    }
}

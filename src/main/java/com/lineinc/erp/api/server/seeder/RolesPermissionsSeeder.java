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
        // 전체권한(삭제 제외) 역할 생성 및 모든 메뉴에 대한 권한 매핑
        Role subMasterRole = roleRepository.findByName(AppConstants.ROLE_SUB_MASTER_NAME)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(AppConstants.ROLE_SUB_MASTER_NAME)
                        .build()));

        EnumSet<PermissionAction> subMasterActions = EnumSet.complementOf(EnumSet.of(PermissionAction.DELETE));

        for (Menu m : menuRepository.findAll()) {
            for (PermissionAction action : subMasterActions) {
                permissionRepository.findByMenuAndAction(m, action).ifPresent(permission -> {
                    subMasterRole.getPermissions().add(permission);
                });
            }
        }

        roleRepository.save(subMasterRole);
    }
}
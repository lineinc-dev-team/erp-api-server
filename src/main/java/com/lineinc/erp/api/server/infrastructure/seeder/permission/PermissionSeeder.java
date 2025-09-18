package com.lineinc.erp.api.server.infrastructure.seeder.permission;

import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Component;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.menu.repository.MenuRepository;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.permission.repository.PermissionRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermissionSeeder {

    private final MenuRepository menuRepository;
    private final PermissionRepository permissionRepository;

    /**
     * 메뉴별로 모든 권한을 중복 없이 시딩한다.
     */
    @Transactional
    public void seed() {
        // 모든 메뉴 조회
        final List<Menu> allMenus = menuRepository.findAll();

        // 모든 권한 타입 가져오기
        final EnumSet<PermissionAction> allActions = EnumSet.allOf(PermissionAction.class);

        // 메뉴별로 모든 권한에 대해 중복 없이 권한 엔티티 생성
        for (final Menu menu : allMenus) {
            for (final PermissionAction action : allActions) {
                final boolean exists = permissionRepository.existsByMenuAndAction(menu, action);
                if (!exists) {
                    final Permission permission = Permission.builder()
                            .menu(menu)
                            .action(action)
                            .createdBy(AppConstants.SYSTEM_NAME)
                            .updatedBy(AppConstants.SYSTEM_NAME)
                            .build();
                    permissionRepository.save(permission);
                }
            }
        }
    }
}
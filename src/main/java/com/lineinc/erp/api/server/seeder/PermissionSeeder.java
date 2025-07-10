package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.common.constant.AppConstants;
import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.menu.repository.MenuRepository;
import com.lineinc.erp.api.server.domain.permission.entity.Permission;
import com.lineinc.erp.api.server.domain.permission.enums.PermissionAction;
import com.lineinc.erp.api.server.domain.permission.repository.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;

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
        List<Menu> allMenus = menuRepository.findAll();

        // 모든 권한 타입 가져오기
        EnumSet<PermissionAction> allActions = EnumSet.allOf(PermissionAction.class);

        // 메뉴별로 모든 권한에 대해 중복 없이 권한 엔티티 생성
        for (Menu menu : allMenus) {
            for (PermissionAction action : allActions) {
                boolean exists = permissionRepository.existsByMenuAndAction(menu, action);
                if (!exists) {
                    Permission permission = Permission.builder()
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
package com.lineinc.erp.api.server.infrastructure.seeder.menu;

import com.lineinc.erp.api.server.shared.constant.AppConstants;
import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuSeeder {

    private final MenuRepository menuRepository;

    public void seed() {
        for (String name : AppConstants.MENU_NAMES) {
            boolean exists = menuRepository.findByName(name).isPresent();
            if (!exists) {
                Menu menu = Menu.builder()
                        .name(name)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build();

                menuRepository.save(menu);
            }
        }
    }
}
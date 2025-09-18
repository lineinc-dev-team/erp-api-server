package com.lineinc.erp.api.server.infrastructure.seeder.menu;

import org.springframework.stereotype.Component;

import com.lineinc.erp.api.server.domain.menu.entity.Menu;
import com.lineinc.erp.api.server.domain.menu.repository.MenuRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MenuSeeder {

    private final MenuRepository menuRepository;

    public void seed() {
        for (final String name : AppConstants.MENU_NAMES) {
            final boolean exists = menuRepository.findByName(name).isPresent();
            if (!exists) {
                final Menu menu = Menu.builder()
                        .name(name)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build();

                menuRepository.save(menu);
            }
        }
    }
}
package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.common.constant.AppConstants;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder {

    private final RoleRepository roleRepository;

    public void seed() {
        String roleName = AppConstants.ROLE_ADMIN_NAME;
        boolean exists = roleRepository.findByName(roleName).isPresent();

        if (!exists) {
            Role role = Role.builder()
                    .name(roleName)
                    .createdBy(AppConstants.SYSTEM_NAME)
                    .updatedBy(AppConstants.SYSTEM_NAME)
                    .build();

            roleRepository.save(role);
        }
    }
}
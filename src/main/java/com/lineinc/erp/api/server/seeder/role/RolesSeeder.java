package com.lineinc.erp.api.server.seeder.role;

import com.lineinc.erp.api.server.shared.constant.AppConstants;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RolesSeeder {

    private final RoleRepository roleRepository;

    public void seed() {
        List<String> roleNames = List.of(
                AppConstants.ROLE_ADMIN_NAME
        );

        for (String roleName : roleNames) {
            boolean exists = roleRepository.findByName(roleName).isPresent();

            if (!exists) {
                Role role = Role.builder()
                        .name(roleName)
                        .hasGlobalSiteProcessAccess(AppConstants.ROLE_ADMIN_NAME.equals(roleName))
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build();

                roleRepository.save(role);
            }
        }
    }
}
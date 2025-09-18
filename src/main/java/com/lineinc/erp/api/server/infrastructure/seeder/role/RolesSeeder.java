package com.lineinc.erp.api.server.infrastructure.seeder.role;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RolesSeeder {

    private final RoleRepository roleRepository;

    public void seed() {
        final List<String> roleNames = List.of(
                AppConstants.ROLE_ADMIN_NAME);

        for (final String roleName : roleNames) {
            final boolean exists = roleRepository.findByName(roleName).isPresent();

            if (!exists) {
                final Role role = Role.builder()
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
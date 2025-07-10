package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.roles.entity.Role;
import com.lineinc.erp.api.server.domain.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RolesSeeder {

    private final RoleRepository roleRepository;

    public void seed() {
        String roleName = "관리자";
        boolean exists = roleRepository.findByName(roleName).isPresent();

        if (!exists) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();

            roleRepository.save(role);
        }
    }
}
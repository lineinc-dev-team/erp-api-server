package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.roles.entity.Roles;
import com.lineinc.erp.api.server.domain.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
@RequiredArgsConstructor
public class RolesSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        String roleName = "관리자";
        boolean exists = roleRepository.findByName(roleName).isPresent();

        if (!exists) {
            Roles roles = Roles.builder()
                    .name(roleName)
                    .build();

            roleRepository.save(roles);
        }
    }
}
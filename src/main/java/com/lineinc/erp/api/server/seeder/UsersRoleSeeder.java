package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.roles.entity.Roles;
import com.lineinc.erp.api.server.domain.roles.repository.RoleRepository;
import com.lineinc.erp.api.server.domain.users.entity.Users;
import com.lineinc.erp.api.server.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Order(3)
public class UsersRoleSeeder implements ApplicationRunner {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;

    @Value("${ADMIN_LOGIN_ID}")
    private String adminLoginId;

    @Override
    public void run(ApplicationArguments args) {
        Optional<Users> adminUserOpt = usersRepository.findByLoginId(adminLoginId);
        Optional<Roles> adminRoleOpt = roleRepository.findByName("ADMIN");  // 'ADMIN' 권한명 가정

        if (adminUserOpt.isPresent() && adminRoleOpt.isPresent()) {
            Users adminUser = adminUserOpt.get();
            Roles adminRole = adminRoleOpt.get();

            // 중복 삽입 방지를 위해 먼저 유저 권한 존재 여부 확인 필요 (생략 가능)
            if (!adminUser.getRoles().contains(adminRole)) {
                adminUser.getRoles().add(adminRole);
                usersRepository.save(adminUser);
            }
        }
    }
}
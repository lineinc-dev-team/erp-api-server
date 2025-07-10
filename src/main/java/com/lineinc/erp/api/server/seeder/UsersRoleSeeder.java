package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.domain.roles.entity.Roles;
import com.lineinc.erp.api.server.domain.roles.repository.RoleRepository;
import com.lineinc.erp.api.server.domain.user.entity.Users;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsersRoleSeeder {

    private final UserRepository usersRepository;
    private final RoleRepository roleRepository;

    @Value("${ADMIN_LOGIN_ID}")
    private String adminLoginId;

    public void seed() {
        Optional<Users> adminUserOpt = usersRepository.findByLoginId(adminLoginId);
        Optional<Roles> adminRoleOpt = roleRepository.findByName("관리자");

        if (adminUserOpt.isPresent() && adminRoleOpt.isPresent()) {
            Users adminUsers = adminUserOpt.get();
            Roles adminRoles = adminRoleOpt.get();

            // 중복 삽입 방지를 위해 먼저 유저 권한 존재 여부 확인 필요 (생략 가능)
            if (!adminUsers.getRoles().contains(adminRoles)) {
                adminUsers.getRoles().add(adminRoles);
                usersRepository.save(adminUsers);
            }
        }
    }
}
package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.common.constant.AppConstants;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsersRolesSeeder {

    private final UserRepository usersRepository;
    private final RoleRepository roleRepository;


    public void seed() {
        Optional<User> adminUserOpt = usersRepository.findByLoginId(AppConstants.ADMIN_LOGIN_ID);
        Optional<Role> adminRoleOpt = roleRepository.findByName(AppConstants.ROLE_MASTER_NAME);
        System.out.println("adminUserOpt = " + adminUserOpt);
        System.out.println("adminRoleOpt = " + adminRoleOpt);
        if (adminUserOpt.isPresent() && adminRoleOpt.isPresent()) {
            User adminUser = adminUserOpt.get();
            Role adminRole = adminRoleOpt.get();

            // 중복 삽입 방지를 위해 먼저 유저 권한 존재 여부 확인 필요 (생략 가능)
            if (!adminUser.getRoles().contains(adminRole)) {
                adminUser.getRoles().add(adminRole);
                usersRepository.save(adminUser);
            }
        }
    }
}
package com.lineinc.erp.api.server.seeder;

import com.lineinc.erp.api.server.common.constant.AppConstants;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsersRolesSeeder {

    private final UserRepository usersRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void seed() {
        Optional<User> adminUserOpt = usersRepository.findByLoginId(AppConstants.ADMIN_LOGIN_ID);
        Optional<Role> adminRoleOpt = roleRepository.findByName(AppConstants.ROLE_ADMIN_NAME);

        if (adminUserOpt.isPresent() && adminRoleOpt.isPresent()) {
            User adminUser = adminUserOpt.get();
            Role adminRole = adminRoleOpt.get();

            if (adminUser.getUserRoles().stream().noneMatch(ur -> ur.getRole().equals(adminRole))) {
                UserRole userRole = UserRole.builder()
                        .user(adminUser)
                        .role(adminRole)
                        .build();
                adminUser.getUserRoles().add(userRole);
                usersRepository.save(adminUser);
            }
        }

        usersRepository.findByLoginId(AppConstants.SUB_ADMIN_LOGIN_ID).ifPresent(user ->
                roleRepository.findByName(AppConstants.ROLE_SUB_ADMIN_NAME).ifPresent(role -> {
                    if (user.getUserRoles().stream().noneMatch(ur -> ur.getRole().equals(role))) {
                        UserRole userRole = UserRole.builder()
                                .user(user)
                                .role(role)
                                .build();
                        user.getUserRoles().add(userRole);
                        usersRepository.save(user);
                    }
                })
        );

        usersRepository.findByLoginId(AppConstants.SUB_ADMIN_LITE_LOGIN_ID).ifPresent(user ->
                roleRepository.findByName(AppConstants.ROLE_SUB_ADMIN_WITHOUT_PERMISSION_MENU).ifPresent(role -> {
                    if (user.getUserRoles().stream().noneMatch(ur -> ur.getRole().equals(role))) {
                        UserRole userRole = UserRole.builder()
                                .user(user)
                                .role(role)
                                .build();
                        user.getUserRoles().add(userRole);
                        usersRepository.save(user);
                    }
                })
        );
    }
}
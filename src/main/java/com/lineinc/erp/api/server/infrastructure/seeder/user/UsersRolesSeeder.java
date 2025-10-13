package com.lineinc.erp.api.server.infrastructure.seeder.user;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lineinc.erp.api.server.domain.role.entity.Role;
import com.lineinc.erp.api.server.domain.role.repository.RoleRepository;
import com.lineinc.erp.api.server.domain.user.entity.User;
import com.lineinc.erp.api.server.domain.user.entity.UserRole;
import com.lineinc.erp.api.server.domain.user.repository.UserRepository;
import com.lineinc.erp.api.server.shared.constant.AppConstants;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsersRolesSeeder {

    private final UserRepository usersRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void seed() {
        final Optional<User> adminUserOpt = usersRepository.findByLoginIdAndDeletedFalse(AppConstants.ADMIN_LOGIN_ID);
        final Optional<Role> adminRoleOpt = roleRepository.findByName(AppConstants.ROLE_ADMIN_NAME);

        if (adminUserOpt.isPresent() && adminRoleOpt.isPresent()) {
            final User adminUser = adminUserOpt.get();
            final Role adminRole = adminRoleOpt.get();

            if (adminUser.getUserRoles().stream().noneMatch(ur -> ur.getRole().equals(adminRole))) {
                final UserRole userRole = UserRole.builder()
                        .user(adminUser)
                        .role(adminRole)
                        .createdBy(AppConstants.SYSTEM_NAME)
                        .updatedBy(AppConstants.SYSTEM_NAME)
                        .build();
                adminUser.getUserRoles().add(userRole);
                usersRepository.save(adminUser);
            }
        }
    }
}
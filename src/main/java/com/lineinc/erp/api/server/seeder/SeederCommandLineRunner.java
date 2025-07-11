package com.lineinc.erp.api.server.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeederCommandLineRunner implements CommandLineRunner {

    private final CompanySeeder companySeeder;
    private final PermissionSeeder permissionSeeder;
    private final MenuSeeder menuSeeder;
    private final RolesSeeder rolesSeeder;
    private final RolesPermissionsSeeder rolesPermissionsSeeder;
    private final UsersRolesSeeder usersRolesSeeder;
    private final UsersSeeder usersSeeder;

    @Value("${seeder.enabled:true}")
    private boolean seederEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (seederEnabled) {
            companySeeder.seed();
            menuSeeder.seed();
            permissionSeeder.seed();
            rolesSeeder.seed();
            rolesPermissionsSeeder.seed();
            usersSeeder.seed();
            usersRolesSeeder.seed();
            System.out.println("Database seeding completed.");
        }
    }
}
package com.lineinc.erp.api.server.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeederCommandLineRunner implements CommandLineRunner {
    private final PermissionSeeder permissionSeeder;
    private final MenuSeeder menuSeeder;
    private final RolesSeeder rolesSeeder;
    private final RolesPermissionsSeeder rolesPermissionsSeeder;
    private final OrganizationSeeder organizationSeeder;
    private final UsersSeeder usersSeeder;
    private final UsersRolesSeeder usersRolesSeeder;

    @Value("${seeder.enabled:false}")
    private boolean seederEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (seederEnabled) {
            menuSeeder.seed();
            permissionSeeder.seed();
            rolesSeeder.seed();
            rolesPermissionsSeeder.seed();
            organizationSeeder.seed();
            usersSeeder.seed();
            usersRolesSeeder.seed();
            System.out.println("Database seeding completed.");
        }
    }
}
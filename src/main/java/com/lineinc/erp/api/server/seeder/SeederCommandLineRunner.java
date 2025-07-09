package com.lineinc.erp.api.server.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class SeederCommandLineRunner implements CommandLineRunner {

    private final CompanySeeder companySeeder;
    private final RolesSeeder rolesSeeder;
    private final UsersRoleSeeder usersRoleSeeder;
    private final UsersSeeder usersSeeder;

    @Value("${seeder.enabled:false}")
    private boolean seederEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (seederEnabled) {
            companySeeder.seed();
            rolesSeeder.seed();
            usersRoleSeeder.seed();
            usersSeeder.seed();
            System.out.println("Database seeding completed.");
        }
    }
}
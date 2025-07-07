package com.lineinc.erp.api.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseSeeder implements ApplicationRunner {

    private final DataSource dataSource;

    @Autowired
    public DatabaseSeeder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        // resources/db/seed/ 경로의 SQL 파일들을 순서대로 실행
        populator.addScripts(
                new ClassPathResource("db/migration/spring_session.sql"),
                new ClassPathResource("db/seed/seed_company.sql"),
                new ClassPathResource("db/seed/seed_role.sql"),
                new ClassPathResource("db/seed/seed_users.sql"),
                new ClassPathResource("db/seed/seed_users_roles.sql")
        );

        populator.execute(dataSource);
    }
}
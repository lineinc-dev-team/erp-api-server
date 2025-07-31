package com.lineinc.erp.api.server.config;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.inmemory.InMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JaversConfig {

    @Bean
    public Javers javers() {
        // DB에 저장하지 않고 메모리에서만 비교 기능 사용
        JaversRepository repository = new InMemoryRepository();
        return JaversBuilder.javers()
                .withObjectAccessHook(new HibernateUnproxyObjectAccessHook<Object>())
                .registerJaversRepository(repository)
                .build();
    }
}
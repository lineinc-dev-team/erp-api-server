package com.lineinc.erp.api.server.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * QueryDSL 사용을 위한 JPAQueryFactory 빈 설정 클래스
 */
@Configuration
public class QuerydslConfig {

    // JPA에서 엔티티 매니저를 주입받음
    private final EntityManager entityManager;

    public QuerydslConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * JPAQueryFactory를 빈으로 등록한다.
     * <p>
     * 이 빈은 Repository 등에서 주입받아 QueryDSL 쿼리를 생성하는 데 사용된다.
     *
     * @return JPAQueryFactory 인스턴스
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
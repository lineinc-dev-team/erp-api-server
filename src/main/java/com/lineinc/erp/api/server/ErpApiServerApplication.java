package com.lineinc.erp.api.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ERP API 서버의 메인 애플리케이션 클래스
 * 
 * Spring Boot 애플리케이션을 시작하고 자동 설정을 활성화합니다.
 * 
 * @SpringBootApplication 어노테이션은 다음을 포함합니다:
 *                        - @Configuration: 설정 클래스임을 명시
 *                        - @EnableAutoConfiguration: Spring Boot 자동 설정 활성화
 *                        - @ComponentScan: 컴포넌트 스캔 활성화
 */
@SpringBootApplication
@EnableScheduling
public class ErpApiServerApplication {

    /**
     * 애플리케이션의 진입점
     * 
     * @param args 명령행 인수
     */
    public static void main(final String[] args) {
        SpringApplication.run(ErpApiServerApplication.class, args);
    }

}

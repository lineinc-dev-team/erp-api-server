package com.lineinc.erp.api.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${swagger.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI erpOpenAPI() {
        return new OpenAPI()
                .components(new Components())                 // 추가적인 인증/스키마 컴포넌트 설정 가능
                .info(new Info()                              // 문서 정보 설정
                        .title("ERP 시스템 API 명세서")
                        .description("ERP 백엔드 REST API 문서입니다.")
                        .version("v1.0"))
                .servers(List.of(
                        new Server().url(serverUrl)               // Swagger 서버 URL 설정 (Base URL)
                ));
    }
}
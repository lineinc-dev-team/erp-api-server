package com.lineinc.erp.api.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI erpOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("ERP 시스템 API 명세서 test")
                        .description("ERP 백엔드 REST API 문서입니다.")
                        .version("v1.0"));
    }
}
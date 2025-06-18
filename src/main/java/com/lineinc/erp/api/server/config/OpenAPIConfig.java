package com.lineinc.erp.api.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI erpOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERP 시스템 API 명세서")
                        .description("ERP 백엔드 REST API 문서입니다.")
                        .version("v1.0"));
    }
}
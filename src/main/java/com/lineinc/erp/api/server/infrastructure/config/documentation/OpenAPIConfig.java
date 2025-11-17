package com.lineinc.erp.api.server.infrastructure.config.documentation;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Bean
    public OpenAPI erpOpenAPI() {
        final String serverUrl = switch (activeProfile) {
            case "dev" -> "https://dev-erp-api.dooson.it";
            case "prod" -> "https://erp-api.dooson.it";
            default -> "http://localhost:8080";
        };

        return new OpenAPI()
                .components(new Components()
                        // 공통 응답 컴포넌트 정의 - 모든 API에서 재사용 가능
                        .addResponses("BadRequest", new ApiResponse()
                                .description("입력값 오류 또는 비즈니스 로직 위반"))
                        .addResponses("Unauthorized", new ApiResponse()
                                .description("인증이 필요함"))
                        .addResponses("Forbidden", new ApiResponse()
                                .description("권한이 없음"))
                        .addResponses("NotFound", new ApiResponse()
                                .description("요청한 리소스를 찾을 수 없음"))
                        .addResponses("InternalServerError", new ApiResponse()
                                .description("서버 내부 오류")))
                .info(new Info() // 문서 정보 설정
                        .title("ERP 시스템 API 명세서")
                        .description("ERP 백엔드 REST API 문서입니다.")
                        .version("v1.0"))
                .servers(List.of(
                        new Server().url(serverUrl) // Swagger 서버 URL 설정 (Base URL)
                ));
    }
}